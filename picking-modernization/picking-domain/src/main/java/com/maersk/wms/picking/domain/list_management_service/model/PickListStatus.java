package com.maersk.wms.picking.domain.list_management_service.model;

/**
 * Pick list status enumeration.
 */
public enum PickListStatus {
    CREATED("0", "Created"),
    RELEASED("1", "Released"),
    ASSIGNED("2", "Assigned"),
    IN_PROGRESS("5", "In Progress"),
    COMPLETED("9", "Completed"),
    SUSPENDED("S", "Suspended"),
    CANCELLED("X", "Cancelled");

    private final String code;
    private final String description;

    PickListStatus(String code, String description) {
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

    public static PickListStatus fromCode(String code) {
        for (PickListStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown pick list status code: " + code);
    }
}
