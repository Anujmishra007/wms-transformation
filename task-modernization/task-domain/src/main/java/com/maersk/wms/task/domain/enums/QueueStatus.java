package com.maersk.wms.task.domain.enums;

/**
 * Task queue status.
 */
public enum QueueStatus {
    ACTIVE("A", "Active", "Queue is active and processing tasks"),
    PAUSED("P", "Paused", "Queue is paused"),
    INACTIVE("I", "Inactive", "Queue is inactive"),
    DRAINING("D", "Draining", "Queue is draining - no new tasks accepted"),
    FULL("F", "Full", "Queue is at capacity");

    private final String code;
    private final String displayName;
    private final String description;

    QueueStatus(String code, String displayName, String description) {
        this.code = code;
        this.displayName = displayName;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static QueueStatus fromCode(String code) {
        for (QueueStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown queue status code: " + code);
    }

    public boolean acceptsNewTasks() {
        return this == ACTIVE;
    }
}
