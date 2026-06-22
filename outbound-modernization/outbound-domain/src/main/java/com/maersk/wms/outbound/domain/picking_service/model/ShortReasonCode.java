package com.maersk.wms.outbound.domain.picking_service.model;

/**
 * Short pick reason codes.
 */
public enum ShortReasonCode {
    INVENTORY_NOT_FOUND("INF", "Inventory Not Found"),
    DAMAGED("DMG", "Product Damaged"),
    EXPIRED("EXP", "Product Expired"),
    QUANTITY_MISMATCH("QTY", "Quantity Mismatch"),
    LOCATION_EMPTY("EMP", "Location Empty"),
    WRONG_PRODUCT("WRP", "Wrong Product in Location"),
    INACCESSIBLE("INA", "Location Inaccessible"),
    SYSTEM_ERROR("SYS", "System Error"),
    OTHER("OTH", "Other");

    private final String code;
    private final String description;

    ShortReasonCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ShortReasonCode fromCode(String code) {
        for (ShortReasonCode reason : values()) {
            if (reason.code.equals(code)) {
                return reason;
            }
        }
        return OTHER;
    }
}
