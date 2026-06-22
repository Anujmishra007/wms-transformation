package com.maersk.wms.task.domain.enums;

/**
 * Assignment lifecycle status.
 */
public enum AssignmentStatus {
    PENDING("0", "Pending", "Assignment pending acceptance"),
    ACCEPTED("1", "Accepted", "Assignment accepted by user"),
    IN_PROGRESS("2", "In Progress", "Task execution in progress"),
    COMPLETED("3", "Completed", "Assignment completed"),
    RELEASED("4", "Released", "Assignment released/unassigned"),
    REASSIGNED("5", "Reassigned", "Assignment transferred to another user"),
    EXPIRED("6", "Expired", "Assignment expired without acceptance");

    private final String code;
    private final String displayName;
    private final String description;

    AssignmentStatus(String code, String displayName, String description) {
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

    public static AssignmentStatus fromCode(String code) {
        for (AssignmentStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown assignment status code: " + code);
    }
}
