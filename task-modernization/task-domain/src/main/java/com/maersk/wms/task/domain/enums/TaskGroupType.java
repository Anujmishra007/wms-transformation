package com.maersk.wms.task.domain.enums;

/**
 * Types of task groups.
 */
public enum TaskGroupType {
    WAVE("Wave", "Pick wave group"),
    BATCH("Batch", "Batch processing group"),
    ROUTE("Route", "Delivery route group"),
    CLUSTER("Cluster", "Cluster pick group"),
    REPLENISHMENT_BATCH("Replenishment Batch", "Replenishment batch group"),
    CYCLE_COUNT_BATCH("Cycle Count Batch", "Cycle count batch group"),
    PUTAWAY_BATCH("Putaway Batch", "Putaway batch group"),
    ZONE_GROUP("Zone Group", "Zone-based task group"),
    USER_ASSIGNMENT("User Assignment", "User-specific task group"),
    CUSTOM("Custom", "Custom task group");

    private final String displayName;
    private final String description;

    TaskGroupType(String displayName, String description) {
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
