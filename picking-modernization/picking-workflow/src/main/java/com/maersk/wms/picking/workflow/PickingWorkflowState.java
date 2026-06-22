package com.maersk.wms.picking.workflow;

/**
 * Picking workflow state enumeration.
 */
public enum PickingWorkflowState {
    INITIALIZED,
    ASSIGNING,
    AWAITING_LOCATION_SCAN,
    VALIDATING_LOCATION,
    AWAITING_SKU_SCAN,
    VALIDATING_SKU,
    AWAITING_CONFIRMATION,
    CONFIRMING,
    COMPLETED,
    COMPENSATING,
    FAILED,
    SKIPPED,
    CANCELLED
}
