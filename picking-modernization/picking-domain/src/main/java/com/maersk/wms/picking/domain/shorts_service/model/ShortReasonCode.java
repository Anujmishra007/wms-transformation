package com.maersk.wms.picking.domain.shorts_service.model;

/**
 * Short pick reason codes.
 */
public enum ShortReasonCode {
    INVENTORY_NOT_FOUND("INF", "Inventory Not Found", true, true),
    QUANTITY_MISMATCH("QTY", "Quantity Mismatch", true, true),
    LOCATION_EMPTY("EMP", "Location Empty", true, true),
    DAMAGED_PRODUCT("DMG", "Product Damaged", true, true),
    EXPIRED_PRODUCT("EXP", "Product Expired", true, true),
    WRONG_PRODUCT("WRP", "Wrong Product in Location", true, true),
    INACCESSIBLE_LOCATION("INA", "Location Inaccessible", false, false),
    EQUIPMENT_ISSUE("EQP", "Equipment Issue", false, false),
    SYSTEM_ERROR("SYS", "System Error", false, false),
    USER_ERROR("USR", "User Error", false, true),
    OTHER("OTH", "Other", false, true);

    private final String code;
    private final String description;
    private final boolean inventoryRelated;
    private final boolean requiresVerification;

    ShortReasonCode(String code, String description, boolean inventoryRelated, boolean requiresVerification) {
        this.code = code;
        this.description = description;
        this.inventoryRelated = inventoryRelated;
        this.requiresVerification = requiresVerification;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public boolean isInventoryRelated() {
        return inventoryRelated;
    }

    public boolean isRequiresVerification() {
        return requiresVerification;
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
