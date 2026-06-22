package com.maersk.wms.inventory.workflow;

import com.maersk.wms.inventory.domain.InventoryAdjustment;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.QueryMethod;

/**
 * Temporal workflow for inventory adjustment operations.
 */
@WorkflowInterface
public interface InventoryAdjustmentWorkflow {

    @WorkflowMethod
    AdjustmentWorkflowResult execute(InventoryAdjustment adjustment);

    @SignalMethod
    void onSupervisorApproval(String supervisorId, boolean approved);

    @QueryMethod
    String getStatus();
}
