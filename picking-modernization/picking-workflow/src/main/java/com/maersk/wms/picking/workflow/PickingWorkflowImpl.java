package com.maersk.wms.picking.workflow;

import com.maersk.wms.picking.activity.PickingActivities;
import com.maersk.wms.picking.domain.PickTask;
import com.maersk.wms.picking.domain.PickConfirmation;
import com.maersk.wms.picking.domain.TaskStatus;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;

/**
 * Implementation of FN839 picking workflow with Saga compensation.
 */
@Slf4j
public class PickingWorkflowImpl implements PickingWorkflow {

    private final PickingActivities activities;

    // Workflow state
    private PickTask task;
    private PickingWorkflowState state;
    private String currentStep;
    private String scannedLocation;
    private String scannedSku;
    private PickConfirmation confirmation;
    private boolean cancelled;
    private boolean skipped;

    public PickingWorkflowImpl() {
        ActivityOptions options = ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofMinutes(5))
                .setRetryOptions(RetryOptions.newBuilder()
                        .setMaximumAttempts(3)
                        .setInitialInterval(Duration.ofSeconds(1))
                        .build())
                .build();

        this.activities = Workflow.newActivityStub(PickingActivities.class, options);
        this.state = PickingWorkflowState.INITIALIZED;
        this.currentStep = "INITIALIZED";
    }

    @Override
    public PickingWorkflowResult execute(PickTask task) {
        this.task = task;

        try {
            // Step 1: Assign task
            state = PickingWorkflowState.ASSIGNING;
            currentStep = "ASSIGNING_TASK";
            activities.assignTask(task.getTaskId(), task.getAssignedUser());

            // Step 2: Wait for location scan
            state = PickingWorkflowState.AWAITING_LOCATION_SCAN;
            currentStep = "WAITING_FOR_LOCATION";
            Workflow.await(() -> scannedLocation != null || cancelled || skipped);

            if (cancelled) {
                return compensate("Task cancelled");
            }
            if (skipped) {
                return PickingWorkflowResult.skipped(task.getTaskId());
            }

            // Step 3: Validate location
            state = PickingWorkflowState.VALIDATING_LOCATION;
            currentStep = "VALIDATING_LOCATION";
            boolean locationValid = activities.validateLocation(scannedLocation, task);
            if (!locationValid) {
                return PickingWorkflowResult.failure(task.getTaskId(), "Invalid location");
            }

            // Step 4: Wait for SKU scan
            state = PickingWorkflowState.AWAITING_SKU_SCAN;
            currentStep = "WAITING_FOR_SKU";
            Workflow.await(() -> scannedSku != null || cancelled);

            if (cancelled) {
                return compensate("Task cancelled after location");
            }

            // Step 5: Validate SKU
            state = PickingWorkflowState.VALIDATING_SKU;
            currentStep = "VALIDATING_SKU";
            boolean skuValid = activities.validateSku(scannedSku, task);
            if (!skuValid) {
                return PickingWorkflowResult.failure(task.getTaskId(), "Invalid SKU");
            }

            // Step 6: Wait for quantity confirmation
            state = PickingWorkflowState.AWAITING_CONFIRMATION;
            currentStep = "WAITING_FOR_QUANTITY";
            Workflow.await(() -> confirmation != null || cancelled);

            if (cancelled) {
                return compensate("Task cancelled before confirmation");
            }

            // Step 7: Confirm pick (Saga transaction)
            state = PickingWorkflowState.CONFIRMING;
            currentStep = "CONFIRMING_PICK";

            // This is a Saga - activities are compensatable
            activities.recordInventoryMovement(task, confirmation);
            activities.updateTaskStatus(task.getTaskId(), TaskStatus.COMPLETED);
            activities.publishPickCompletedEvent(task, confirmation);

            state = PickingWorkflowState.COMPLETED;
            currentStep = "COMPLETED";

            return PickingWorkflowResult.success(task.getTaskId(), confirmation.getPickedQty());

        } catch (Exception e) {
            return compensate("Error: " + e.getMessage());
        }
    }

    private PickingWorkflowResult compensate(String reason) {
        state = PickingWorkflowState.COMPENSATING;
        currentStep = "COMPENSATING";

        try {
            // Saga compensation - reverse any committed changes
            activities.compensateInventoryMovement(task);
            activities.updateTaskStatus(task.getTaskId(), TaskStatus.RELEASED);
        } catch (Exception e) {
            // Log but don't fail compensation
            Workflow.getLogger(PickingWorkflowImpl.class).error("Compensation error", e);
        }

        state = PickingWorkflowState.FAILED;
        return PickingWorkflowResult.failure(task.getTaskId(), reason);
    }

    @Override
    public void onLocationScanned(String barcode) {
        this.scannedLocation = barcode;
    }

    @Override
    public void onSkuScanned(String barcode) {
        this.scannedSku = barcode;
    }

    @Override
    public void onQuantityConfirmed(PickConfirmation confirmation) {
        this.confirmation = confirmation;
    }

    @Override
    public void onShortPick(PickConfirmation confirmation) {
        this.confirmation = confirmation;
    }

    @Override
    public void onSkipTask(String reason) {
        this.skipped = true;
    }

    @Override
    public void onCancelTask(String reason) {
        this.cancelled = true;
    }

    @Override
    public PickingWorkflowState getState() {
        return state;
    }

    @Override
    public String getCurrentStep() {
        return currentStep;
    }
}
