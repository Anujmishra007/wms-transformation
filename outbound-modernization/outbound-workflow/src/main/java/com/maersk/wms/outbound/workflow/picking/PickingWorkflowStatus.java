package com.maersk.wms.outbound.workflow.picking;

/**
 * Status of picking workflow.
 */
public enum PickingWorkflowStatus {
    INITIALIZED,
    LOADING_PICKS,
    AWAITING_PICK,
    PICKING,
    CONFIRMING,
    COMPLETED,
    PAUSED,
    FAILED,
    CANCELLED
}
