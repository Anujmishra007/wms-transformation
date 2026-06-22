package com.maersk.wms.task.domain.enums;

/**
 * Task lifecycle status.
 */
public enum TaskStatus {
    CREATED("0", "Created", "Task has been created but not yet released"),
    RELEASED("1", "Released", "Task released and available for assignment"),
    ASSIGNED("2", "Assigned", "Task assigned to a user"),
    ACCEPTED("3", "Accepted", "Task accepted by assigned user"),
    IN_PROGRESS("4", "In Progress", "Task execution in progress"),
    PARTIALLY_COMPLETE("5", "Partially Complete", "Task partially completed"),
    COMPLETED("6", "Completed", "Task successfully completed"),
    SHORT("7", "Short", "Task completed with shortage"),
    CANCELLED("8", "Cancelled", "Task has been cancelled"),
    ON_HOLD("9", "On Hold", "Task is on hold"),
    FAILED("F", "Failed", "Task execution failed");

    private final String code;
    private final String displayName;
    private final String description;

    TaskStatus(String code, String displayName, String description) {
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

    public static TaskStatus fromCode(String code) {
        for (TaskStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown task status code: " + code);
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == SHORT || this == CANCELLED || this == FAILED;
    }

    public boolean isActive() {
        return this == ASSIGNED || this == ACCEPTED || this == IN_PROGRESS || this == PARTIALLY_COMPLETE;
    }

    public boolean canBeAssigned() {
        return this == CREATED || this == RELEASED;
    }
}
