package com.maersk.wms.task.domain.enums;

/**
 * Task group lifecycle status.
 */
public enum TaskGroupStatus {
    CREATED("0", "Created", "Group created but not active"),
    RELEASED("1", "Released", "Group released for processing"),
    IN_PROGRESS("2", "In Progress", "Group processing in progress"),
    PARTIALLY_COMPLETE("3", "Partially Complete", "Some tasks completed"),
    COMPLETED("4", "Completed", "All tasks completed"),
    CANCELLED("5", "Cancelled", "Group cancelled"),
    ON_HOLD("6", "On Hold", "Group on hold");

    private final String code;
    private final String displayName;
    private final String description;

    TaskGroupStatus(String code, String displayName, String description) {
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

    public static TaskGroupStatus fromCode(String code) {
        for (TaskGroupStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown task group status code: " + code);
    }
}
