package com.maersk.wms.outbound.domain;

/**
 * Order detail status enumeration.
 * Maps to STATUS field in ORDERDETAIL table.
 */
public enum OrderDetailStatus {

    NEW("0", "New"),
    OPEN("02", "Open"),
    PARTIALLY_ALLOCATED("04", "Partially Allocated"),
    ALLOCATED("05", "Fully Allocated"),
    RELEASED("09", "Released to Pick"),
    PICKED("25", "Picked"),
    PACKED("57", "Packed"),
    SHIPPED("95", "Shipped"),
    CLOSED("100", "Closed"),
    CANCELLED("99", "Cancelled");

    private final String code;
    private final String description;

    OrderDetailStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static OrderDetailStatus fromCode(String code) {
        for (OrderDetailStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown order detail status code: " + code);
    }
}
