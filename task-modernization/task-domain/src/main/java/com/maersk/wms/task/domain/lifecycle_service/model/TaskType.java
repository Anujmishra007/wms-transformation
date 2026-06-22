package com.maersk.wms.task.domain.lifecycle_service.model;

/**
 * Enumeration of task types.
 */
public enum TaskType {
    // Inbound tasks
    RECEIVING("Receiving tasks"),
    PUTAWAY("Putaway tasks"),
    CROSS_DOCK("Cross-docking tasks"),

    // Outbound tasks
    PICK("Picking tasks"),
    PACK("Packing tasks"),
    SHIP("Shipping tasks"),
    LOADING("Loading tasks"),

    // Inventory tasks
    REPLENISHMENT("Replenishment tasks"),
    CYCLE_COUNT("Cycle count tasks"),
    PHYSICAL_COUNT("Physical inventory count"),
    INVENTORY_MOVE("Inventory movement"),
    CONSOLIDATION("Consolidation tasks"),

    // Returns
    RETURN_RECEIVE("Return receiving"),
    RETURN_PUTAWAY("Return putaway"),
    RETURN_INSPECTION("Return inspection"),

    // Special
    QUALITY_INSPECTION("Quality inspection"),
    REPACK("Repacking tasks"),
    RELABEL("Relabeling tasks"),
    CUSTOM("Custom tasks");

    private final String description;

    TaskType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isInbound() {
        return this == RECEIVING || this == PUTAWAY || this == CROSS_DOCK;
    }

    public boolean isOutbound() {
        return this == PICK || this == PACK || this == SHIP || this == LOADING;
    }

    public boolean isInventory() {
        return this == REPLENISHMENT || this == CYCLE_COUNT || this == PHYSICAL_COUNT ||
               this == INVENTORY_MOVE || this == CONSOLIDATION;
    }
}
