package com.maersk.wms.outbound.workflow.allocation;

import com.maersk.wms.outbound.activity.AllocationActivities;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of AllocationWorkflow.
 * Orchestrates Order Allocation → Inventory Reservation → Pick Header Creation.
 */
public class AllocationWorkflowImpl implements AllocationWorkflow {

    private final AllocationActivities activities;

    // Workflow state
    private AllocationWorkflowStatus status = AllocationWorkflowStatus.INITIALIZED;
    private AllocationWorkflowState state;

    // Signal flags
    private boolean pauseSignal = false;
    private boolean resumeSignal = false;
    private boolean cancelSignal = false;
    private Boolean partialApproval = null;
    private String pauseReason;
    private String cancelReason;

    // Results tracking
    private final List<String> pickHeaderKeys = new ArrayList<>();
    private final Map<String, BigDecimal> shortagesBySku = new HashMap<>();
    private final List<String> warnings = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();

    public AllocationWorkflowImpl() {
        ActivityOptions options = ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofMinutes(10))
                .setRetryOptions(RetryOptions.newBuilder()
                        .setInitialInterval(Duration.ofSeconds(1))
                        .setMaximumInterval(Duration.ofMinutes(1))
                        .setBackoffCoefficient(2.0)
                        .setMaximumAttempts(3)
                        .build())
                .build();

        this.activities = Workflow.newActivityStub(AllocationActivities.class, options);
    }

    @Override
    public AllocationWorkflowResult execute(AllocationWorkflowRequest request) {
        long startTime = System.currentTimeMillis();

        try {
            // Initialize state
            state = AllocationWorkflowState.builder()
                    .waveKey(request.getWaveKey())
                    .totalOrders(request.getOrderKeys().size())
                    .processedOrders(0)
                    .totalAllocated(BigDecimal.ZERO)
                    .totalShortage(BigDecimal.ZERO)
                    .startTime(LocalDateTime.now())
                    .build();

            // ==================== PHASE 1: VALIDATION ====================
            status = AllocationWorkflowStatus.VALIDATING;
            boolean valid = activities.validateAllocationRequest(
                    request.getWaveKey(),
                    request.getOrderKeys(),
                    request.getUserId()
            );

            if (!valid) {
                status = AllocationWorkflowStatus.FAILED;
                return buildFailureResult(startTime, "Validation failed");
            }

            if (cancelSignal) {
                return handleCancellation(startTime);
            }

            // ==================== PHASE 2: ALLOCATION ====================
            status = AllocationWorkflowStatus.ALLOCATING;

            for (String orderKey : request.getOrderKeys()) {
                // Check for pause/cancel
                if (pauseSignal) {
                    status = AllocationWorkflowStatus.PAUSED;
                    Workflow.await(() -> resumeSignal || cancelSignal);
                    if (cancelSignal) {
                        return handleCancellation(startTime);
                    }
                    pauseSignal = false;
                    resumeSignal = false;
                    status = AllocationWorkflowStatus.ALLOCATING;
                }

                if (cancelSignal) {
                    return handleCancellation(startTime);
                }

                state.setCurrentOrderKey(orderKey);

                try {
                    // Allocate order using strategy method
                    var result = activities.allocateOrderWithStrategy(
                            orderKey,
                            request.getPreferredStrategy(),
                            request.isAllowPartialAllocation(),
                            request.getUserId()
                    );

                    // Track results
                    if (result.getPickHeaderKeys() != null) {
                        pickHeaderKeys.addAll(result.getPickHeaderKeys());
                    }
                    if (result.getAllocatedQty() != null) {
                        state.setTotalAllocated(state.getTotalAllocated().add(result.getAllocatedQty()));
                    }
                    if (result.getShortageQty() != null) {
                        state.setTotalShortage(state.getTotalShortage().add(result.getShortageQty()));
                    }

                    if (result.getShortagesBySku() != null && !result.getShortagesBySku().isEmpty()) {
                        result.getShortagesBySku().forEach((sku, qty) ->
                                shortagesBySku.merge(sku, qty, BigDecimal::add));
                    }

                    state.setProcessedOrders(state.getProcessedOrders() + 1);

                } catch (Exception e) {
                    errors.add("Failed to allocate order " + orderKey + ": " + e.getMessage());
                    state.setLastError(e.getMessage());
                }
            }

            // ==================== PHASE 3: APPROVAL (if needed) ====================
            if (!shortagesBySku.isEmpty() && request.isRequireApprovalForPartial()) {
                status = AllocationWorkflowStatus.AWAITING_APPROVAL;
                Workflow.await(() -> partialApproval != null || cancelSignal);

                if (cancelSignal) {
                    return handleCancellation(startTime);
                }

                if (!partialApproval) {
                    // Roll back allocation
                    activities.unallocateWave(request.getWaveKey(), request.getUserId());
                    status = AllocationWorkflowStatus.CANCELLED;
                    return buildFailureResult(startTime, "Partial allocation not approved");
                }
            }

            // ==================== PHASE 4: RELEASE (if auto-release) ====================
            if (request.isAutoRelease() && !pickHeaderKeys.isEmpty()) {
                status = AllocationWorkflowStatus.RELEASING;
                activities.releasePickHeaders(pickHeaderKeys, request.getUserId());
            }

            status = AllocationWorkflowStatus.COMPLETED;
            return buildSuccessResult(startTime);

        } catch (Exception e) {
            status = AllocationWorkflowStatus.FAILED;
            state.setLastError(e.getMessage());
            errors.add(e.getMessage());
            return buildFailureResult(startTime, e.getMessage());
        }
    }

    // ==================== SIGNAL HANDLERS ====================

    @Override
    public void pauseAllocation(String reason) {
        pauseSignal = true;
        pauseReason = reason;
    }

    @Override
    public void resumeAllocation() {
        resumeSignal = true;
    }

    @Override
    public void cancelAllocation(String reason) {
        cancelSignal = true;
        cancelReason = reason;
    }

    @Override
    public void approvePartialAllocation(boolean approved) {
        partialApproval = approved;
    }

    // ==================== QUERY HANDLERS ====================

    @Override
    public AllocationWorkflowStatus getStatus() {
        return status;
    }

    @Override
    public AllocationWorkflowState getState() {
        return state;
    }

    @Override
    public AllocationProgress getProgress() {
        return AllocationProgress.builder()
                .waveKey(state.getWaveKey())
                .totalOrders(state.getTotalOrders())
                .processedOrders(state.getProcessedOrders())
                .percentComplete(state.getTotalOrders() > 0
                        ? (double) state.getProcessedOrders() / state.getTotalOrders() * 100
                        : 0)
                .status(status)
                .build();
    }

    // ==================== PRIVATE METHODS ====================

    private AllocationWorkflowResult handleCancellation(long startTime) {
        status = AllocationWorkflowStatus.CANCELLED;
        return AllocationWorkflowResult.builder()
                .waveKey(state.getWaveKey())
                .success(false)
                .fullyAllocated(false)
                .status(status)
                .message("Allocation cancelled: " + cancelReason)
                .executionTimeMs(System.currentTimeMillis() - startTime)
                .build();
    }

    private AllocationWorkflowResult buildSuccessResult(long startTime) {
        return AllocationWorkflowResult.builder()
                .waveKey(state.getWaveKey())
                .success(true)
                .fullyAllocated(shortagesBySku.isEmpty())
                .status(status)
                .totalAllocated(state.getTotalAllocated())
                .totalShortage(state.getTotalShortage())
                .ordersProcessed(state.getProcessedOrders())
                .pickHeadersCreated(pickHeaderKeys.size())
                .pickHeaderKeys(pickHeaderKeys)
                .shortagesBySku(shortagesBySku)
                .warnings(warnings)
                .errors(errors)
                .message("Allocation completed successfully")
                .executionTimeMs(System.currentTimeMillis() - startTime)
                .build();
    }

    private AllocationWorkflowResult buildFailureResult(long startTime, String message) {
        return AllocationWorkflowResult.builder()
                .waveKey(state.getWaveKey())
                .success(false)
                .fullyAllocated(false)
                .status(status)
                .totalAllocated(state.getTotalAllocated())
                .totalShortage(state.getTotalShortage())
                .ordersProcessed(state.getProcessedOrders())
                .pickHeaderKeys(pickHeaderKeys)
                .shortagesBySku(shortagesBySku)
                .warnings(warnings)
                .errors(errors)
                .message(message)
                .executionTimeMs(System.currentTimeMillis() - startTime)
                .build();
    }
}
