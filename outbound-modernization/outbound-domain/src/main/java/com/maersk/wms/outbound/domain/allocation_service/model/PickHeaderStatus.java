package com.maersk.wms.outbound.domain.allocation_service.model;

/**
 * PickHeader status enumeration.
 */
public enum PickHeaderStatus {
    CREATED("0", "Created"),
    RELEASED("1", "Released"),
    IN_PROGRESS("5", "In Progress"),
    COMPLETED("9", "Completed"),
    CANCELLED("X", "Cancelled");

    private final String code;
    private final String description;

    PickHeaderStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static PickHeaderStatus fromCode(String code) {
        for (PickHeaderStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown pick header status code: " + code);
    }
}
