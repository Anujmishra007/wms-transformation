package com.maersk.wms.outbound.domain;

/**
 * Shipment status enumeration.
 */
public enum ShipmentStatus {

    NEW("0", "New"),
    CREATED("5", "Created"),
    PACKED("10", "Packed"),
    STAGED("15", "Staged"),
    MANIFESTED("20", "Manifested"),
    LOADED("25", "Loaded"),
    SHIPPED("30", "Shipped"),
    DELIVERED("35", "Delivered"),
    CANCELLED("99", "Cancelled");

    private final String code;
    private final String description;

    ShipmentStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ShipmentStatus fromCode(String code) {
        for (ShipmentStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown shipment status code: " + code);
    }
}
