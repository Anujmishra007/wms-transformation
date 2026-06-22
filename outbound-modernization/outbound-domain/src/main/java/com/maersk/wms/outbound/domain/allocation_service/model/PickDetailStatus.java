package com.maersk.wms.outbound.domain.allocation_service.model;

/**
 * PickDetail status enumeration.
 * Tracks the lifecycle of a pick task.
 */
public enum PickDetailStatus {
    ALLOCATED("0", "Allocated"),
    RELEASED("1", "Released for Picking"),
    IN_PROGRESS("5", "In Progress"),
    COMPLETED("9", "Completed"),
    SHORTED("S", "Shorted"),
    CANCELLED("X", "Cancelled");

    private final String code;
    private final String description;

    PickDetailStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public boolean isReceived() {
        return this == COMPLETED;
    }

    public boolean isOpen() {
        return this == ALLOCATED || this == RELEASED || this == IN_PROGRESS;
    }

    public static PickDetailStatus fromCode(String code) {
        for (PickDetailStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown pick detail status code: " + code);
    }
}
