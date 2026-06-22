package com.maersk.wms.outbound.workflow.allocation;

/**
 * Status of allocation workflow.
 */
public enum AllocationWorkflowStatus {
    INITIALIZED,
    VALIDATING,
    ALLOCATING,
    AWAITING_APPROVAL,
    RELEASING,
    COMPLETED,
    PAUSED,
    FAILED,
    CANCELLED
}
