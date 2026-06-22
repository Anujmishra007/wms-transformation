package com.maersk.wms.picking.domain.task_execution_service.model;

/**
 * Pick task status enumeration.
 */
public enum PickTaskStatus {
    CREATED("0", "Created"),
    RELEASED("1", "Released"),
    ASSIGNED("2", "Assigned"),
    IN_PROGRESS("5", "In Progress"),
    COMPLETED("9", "Completed"),
    SHORTED("S", "Shorted"),
    CANCELLED("X", "Cancelled"),
    SUSPENDED("P", "Suspended");

    private final String code;
    private final String description;

    PickTaskStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public boolean isOpen() {
        return this == CREATED || this == RELEASED || this == ASSIGNED || this == IN_PROGRESS;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED;
    }

    public static PickTaskStatus fromCode(String code) {
        for (PickTaskStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown pick task status code: " + code);
    }
}
