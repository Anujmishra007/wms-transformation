package com.maersk.wms.outbound.domain.order_service.model;

/**
 * Order status enumeration.
 */
public enum OrderStatus {
    OPEN("0", "Open"),
    HOLD("1", "Hold"),
    RELEASED("2", "Released to Wave"),
    PARTIALLY_ALLOCATED("3", "Partially Allocated"),
    ALLOCATED("4", "Fully Allocated"),
    IN_PICKING("5", "In Picking"),
    PICKED("6", "Picked"),
    PACKED("7", "Packed"),
    LOADED("8", "Loaded"),
    SHIPPED("9", "Shipped"),
    CANCELLED("X", "Cancelled");

    private final String code;
    private final String description;

    OrderStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static OrderStatus fromCode(String code) {
        for (OrderStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown order status code: " + code);
    }
}
