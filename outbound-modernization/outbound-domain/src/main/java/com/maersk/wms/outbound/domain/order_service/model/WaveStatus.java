package com.maersk.wms.outbound.domain.order_service.model;

/**
 * Wave status enumeration.
 */
public enum WaveStatus {
    PLANNED("0", "Planned"),
    RELEASED("1", "Released"),
    ALLOCATING("2", "Allocating"),
    ALLOCATED("3", "Allocated"),
    IN_PICKING("4", "In Picking"),
    PICKED("5", "Picked"),
    COMPLETED("9", "Completed"),
    CANCELLED("X", "Cancelled");

    private final String code;
    private final String description;

    WaveStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static WaveStatus fromCode(String code) {
        for (WaveStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown wave status code: " + code);
    }
}
