package com.maersk.wms.picking.workflow;

import com.maersk.wms.picking.domain.PickTask;
import com.maersk.wms.picking.domain.PickConfirmation;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.QueryMethod;

/**
 * Temporal workflow interface for FN839 Piece Pick operations.
 *
 * Orchestrates the complete pick cycle:
 * 1. Get/Assign Task
 * 2. Navigate to Location
 * 3. Decode Location Barcode
 * 4. Decode SKU Barcode
 * 5. Confirm Pick (Full/Short)
 * 6. Update Inventory
 * 7. Complete or Next Task
 *
 * Supports Saga pattern for compensating transactions.
 */
@WorkflowInterface
public interface PickingWorkflow {

    /**
     * Execute the complete pick workflow.
     *
     * @param task The pick task to execute
     * @return Workflow result with completion status
     */
    @WorkflowMethod
    PickingWorkflowResult execute(PickTask task);

    /**
     * Signal: Location barcode scanned.
     */
    @SignalMethod
    void onLocationScanned(String barcode);

    /**
     * Signal: SKU barcode scanned.
     */
    @SignalMethod
    void onSkuScanned(String barcode);

    /**
     * Signal: Quantity confirmed.
     */
    @SignalMethod
    void onQuantityConfirmed(PickConfirmation confirmation);

    /**
     * Signal: Short pick requested.
     */
    @SignalMethod
    void onShortPick(PickConfirmation confirmation);

    /**
     * Signal: Skip task requested.
     */
    @SignalMethod
    void onSkipTask(String reason);

    /**
     * Signal: Cancel task requested.
     */
    @SignalMethod
    void onCancelTask(String reason);

    /**
     * Query: Get current workflow state.
     */
    @QueryMethod
    PickingWorkflowState getState();

    /**
     * Query: Get current step in workflow.
     */
    @QueryMethod
    String getCurrentStep();
}
