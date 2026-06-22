package com.maersk.wms.outbound.domain.order_service.model;

/**
 * Order detail status enumeration.
 */
public enum OrderDetailStatus {
    OPEN("0", "Open"),
    HOLD("1", "Hold"),
    RELEASED("2", "Released"),
    PARTIALLY_ALLOCATED("3", "Partially Allocated"),
    ALLOCATED("4", "Allocated"),
    IN_PICKING("5", "In Picking"),
    PICKED("6", "Picked"),
    PACKED("7", "Packed"),
    SHIPPED("9", "Shipped"),
    CANCELLED("X", "Cancelled");

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
