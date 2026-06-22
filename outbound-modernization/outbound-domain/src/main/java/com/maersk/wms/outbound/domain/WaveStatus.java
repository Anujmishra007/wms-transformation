package com.maersk.wms.outbound.domain;

/**
 * Wave status enumeration.
 */
public enum WaveStatus {

    PLANNED("0", "Planned"),
    RELEASED("5", "Released"),
    IN_PROGRESS("10", "In Progress"),
    PICKING("15", "Picking"),
    PACKING("20", "Packing"),
    COMPLETED("25", "Completed"),
    CANCELLED("99", "Cancelled");

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
