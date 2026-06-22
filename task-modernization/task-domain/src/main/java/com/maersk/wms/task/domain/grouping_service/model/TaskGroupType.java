package com.maersk.wms.task.domain.grouping_service.model;

/**
 * Enumeration of task group types.
 */
public enum TaskGroupType {
    WAVE("Wave-based grouping for outbound tasks"),
    BATCH("Batch grouping for similar tasks"),
    ZONE("Zone-based grouping for area assignments"),
    ROUTE("Route-based grouping for delivery paths"),
    PICK_LIST("Pick list grouping"),
    RECEIPT("Receipt-based grouping for inbound"),
    SHIPMENT("Shipment-based grouping"),
    CLUSTER("Cluster grouping for multi-order picks"),
    CUSTOM("Custom grouping");

    private final String description;

    TaskGroupType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isOutbound() {
        return this == WAVE || this == PICK_LIST || this == CLUSTER || this == SHIPMENT;
    }

    public boolean isInbound() {
        return this == RECEIPT;
    }
}
