package com.maersk.wms.masterdata.domain;

/**
 * Carrier type enumeration.
 */
public enum CarrierType {
    PARCEL("PAR", "Parcel Carrier"),
    LTL("LTL", "Less Than Truckload"),
    TL("TL", "Truckload"),
    AIR("AIR", "Air Freight"),
    OCEAN("OCN", "Ocean Freight"),
    RAIL("RAL", "Rail"),
    COURIER("COR", "Courier/Express"),
    INTERMODAL("INT", "Intermodal"),
    CUSTOMER_PICKUP("CPU", "Customer Pickup"),
    INTERNAL("INT", "Internal/Private Fleet");

    private final String code;
    private final String description;

    CarrierType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static CarrierType fromCode(String code) {
        for (CarrierType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return PARCEL;
    }
}
