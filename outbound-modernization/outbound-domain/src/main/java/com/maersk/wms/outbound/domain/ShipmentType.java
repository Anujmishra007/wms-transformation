package com.maersk.wms.outbound.domain;

/**
 * Shipment type enumeration.
 */
public enum ShipmentType {

    PARCEL("PAR", "Parcel"),
    LTL("LTL", "Less Than Truckload"),
    TL("TL", "Truckload"),
    AIR("AIR", "Air Freight"),
    OCEAN("OCN", "Ocean Freight"),
    COURIER("COR", "Courier"),
    WILL_CALL("WC", "Will Call / Customer Pickup");

    private final String code;
    private final String description;

    ShipmentType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ShipmentType fromCode(String code) {
        for (ShipmentType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown shipment type code: " + code);
    }
}
