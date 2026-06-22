package com.maersk.wms.task.domain.enums;

/**
 * Task priority levels.
 */
public enum TaskPriority {
    CRITICAL(1, "Critical", "Highest priority - immediate action required"),
    URGENT(2, "Urgent", "High priority - time sensitive"),
    HIGH(3, "High", "Above normal priority"),
    NORMAL(4, "Normal", "Standard priority"),
    LOW(5, "Low", "Below normal priority"),
    DEFERRED(6, "Deferred", "Lowest priority - can be delayed");

    private final int value;
    private final String displayName;
    private final String description;

    TaskPriority(int value, String displayName, String description) {
        this.value = value;
        this.displayName = displayName;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static TaskPriority fromValue(int value) {
        for (TaskPriority priority : values()) {
            if (priority.value == value) {
                return priority;
            }
        }
        return NORMAL;
    }

    public boolean isHigherThan(TaskPriority other) {
        return this.value < other.value;
    }

    public TaskPriority escalate() {
        int newValue = Math.max(1, this.value - 1);
        return fromValue(newValue);
    }
}
