package com.maersk.wms.outbound.domain;

/**
 * Order status enumeration.
 * Maps to STATUS field in ORDERS table (0-9 pattern).
 */
public enum OrderStatus {

    NEW("0", "New"),
    OPEN("02", "Open"),
    PARTIALLY_ALLOCATED("04", "Partially Allocated"),
    ALLOCATED("05", "Fully Allocated"),
    RELEASED("09", "Released to Pick"),
    IN_PICKING("15", "In Picking"),
    PICKED("25", "Picked"),
    IN_PACKING("55", "In Packing"),
    PACKED("57", "Packed"),
    STAGED("60", "Staged"),
    LOADING("62", "Loading"),
    LOADED("65", "Loaded"),
    SHIPPED("95", "Shipped"),
    CLOSED("100", "Closed"),
    CANCELLED("99", "Cancelled"),
    ON_HOLD("HOLD", "On Hold");

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
