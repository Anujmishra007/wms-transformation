package com.maersk.wms.task.domain.enums;

/**
 * Types of tasks in the warehouse.
 */
public enum TaskType {
    // Picking tasks
    PICK("Pick", "Standard picking task"),
    CLUSTER_PICK("Cluster Pick", "Cluster picking task for multiple orders"),
    BATCH_PICK("Batch Pick", "Batch picking task"),
    ZONE_PICK("Zone Pick", "Zone-based picking task"),
    WAVE_PICK("Wave Pick", "Wave-based picking task"),

    // Replenishment tasks
    REPLENISHMENT("Replenishment", "Standard replenishment task"),
    MIN_MAX_REPLENISHMENT("Min/Max Replenishment", "Min/max triggered replenishment"),
    DEMAND_REPLENISHMENT("Demand Replenishment", "Demand-driven replenishment"),
    EMERGENCY_REPLENISHMENT("Emergency Replenishment", "Emergency replenishment"),

    // Putaway tasks
    PUTAWAY("Putaway", "Standard putaway task"),
    DIRECTED_PUTAWAY("Directed Putaway", "System-directed putaway"),
    CROSS_DOCK("Cross Dock", "Cross-docking task"),

    // Movement tasks
    MOVE("Move", "Standard move task"),
    PALLET_MOVE("Pallet Move", "Full pallet movement"),
    CASE_MOVE("Case Move", "Case movement"),
    CONSOLIDATION("Consolidation", "Inventory consolidation"),

    // Inventory tasks
    CYCLE_COUNT("Cycle Count", "Cycle count task"),
    PHYSICAL_INVENTORY("Physical Inventory", "Physical inventory count"),
    AUDIT("Audit", "Inventory audit task"),
    ADJUSTMENT("Adjustment", "Inventory adjustment task"),

    // Packing tasks
    PACK("Pack", "Standard packing task"),
    REPACK("Repack", "Repacking task"),
    LABELING("Labeling", "Labeling task"),

    // Loading tasks
    LOAD("Load", "Loading task"),
    STAGING("Staging", "Staging for shipment"),

    // Quality tasks
    QC_INSPECTION("QC Inspection", "Quality control inspection"),
    RETURNS_PROCESSING("Returns Processing", "Returns processing task"),

    // Maintenance tasks
    EQUIPMENT_CHECKOUT("Equipment Checkout", "Equipment checkout"),
    EQUIPMENT_CHECKIN("Equipment Checkin", "Equipment check-in");

    private final String displayName;
    private final String description;

    TaskType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
