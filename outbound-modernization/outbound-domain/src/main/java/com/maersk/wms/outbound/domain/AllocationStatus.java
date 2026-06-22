package com.maersk.wms.outbound.domain;

/**
 * Allocation status enumeration.
 */
public enum AllocationStatus {

    ALLOCATED("0", "Allocated"),
    RELEASED("5", "Released to Pick"),
    IN_PROGRESS("10", "Pick In Progress"),
    PICKED("15", "Picked"),
    SHORTED("20", "Shorted"),
    PACKED("25", "Packed"),
    SHIPPED("30", "Shipped"),
    CANCELLED("99", "Cancelled");

    private final String code;
    private final String description;

    AllocationStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AllocationStatus fromCode(String code) {
        for (AllocationStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown allocation status code: " + code);
    }
}
