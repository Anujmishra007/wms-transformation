package com.maersk.wms.outbound.workflow;

/**
 * Status of wave planning workflow.
 */
public enum WavePlanningStatus {
    PENDING,
    PLANNING,
    PLANNED,
    RELEASING,
    RELEASED,
    ALLOCATING,
    ALLOCATED,
    COMPLETED,
    FAILED,
    CANCELLED
}
