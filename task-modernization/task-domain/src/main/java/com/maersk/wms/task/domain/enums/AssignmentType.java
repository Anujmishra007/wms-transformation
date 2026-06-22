package com.maersk.wms.task.domain.enums;

/**
 * Types of task assignment.
 */
public enum AssignmentType {
    MANUAL("Manual", "Manually assigned by supervisor"),
    AUTO("Auto", "System auto-assigned"),
    SELF("Self", "Self-assigned by user"),
    QUEUE("Queue", "Assigned from queue"),
    DIRECTED("Directed", "System directed assignment"),
    ROUND_ROBIN("Round Robin", "Round robin assignment"),
    LOAD_BALANCED("Load Balanced", "Load balanced assignment"),
    ZONE_BASED("Zone Based", "Zone-based assignment");

    private final String displayName;
    private final String description;

    AssignmentType(String displayName, String description) {
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
