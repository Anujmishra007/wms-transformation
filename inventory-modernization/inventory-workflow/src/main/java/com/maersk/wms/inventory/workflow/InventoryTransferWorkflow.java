package com.maersk.wms.inventory.workflow;

import com.maersk.wms.inventory.domain.InventoryTransfer;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.QueryMethod;

/**
 * Temporal workflow for inventory transfer operations.
 */
@WorkflowInterface
public interface InventoryTransferWorkflow {

    @WorkflowMethod
    TransferWorkflowResult execute(InventoryTransfer transfer);

    @SignalMethod
    void onSourceConfirmed(String lpn);

    @SignalMethod
    void onDestinationConfirmed(String location);

    @QueryMethod
    String getStatus();
}
