package com.maersk.wms.outbound.workflow;

/**
 * Status of order fulfillment workflow.
 */
public enum OrderFulfillmentStatus {
    PENDING,
    ALLOCATING,
    ALLOCATED,
    ALLOCATION_SHORT,
    PICKING,
    PICKED,
    PACKING,
    PACKED,
    SHIPPING,
    SHIPPED,
    COMPLETED,
    FAILED,
    CANCELLED
}
