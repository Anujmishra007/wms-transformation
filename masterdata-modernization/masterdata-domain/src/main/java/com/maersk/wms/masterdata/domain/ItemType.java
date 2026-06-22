package com.maersk.wms.masterdata.domain;

/**
 * Item type enumeration.
 */
public enum ItemType {
    FINISHED_GOOD("FG", "Finished Good"),
    RAW_MATERIAL("RM", "Raw Material"),
    COMPONENT("CP", "Component"),
    PACKAGING("PK", "Packaging Material"),
    CONSUMABLE("CS", "Consumable"),
    SERVICE("SV", "Service Item"),
    KIT("KT", "Kit/Bundle"),
    VIRTUAL("VT", "Virtual/Non-Stock");

    private final String code;
    private final String description;

    ItemType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ItemType fromCode(String code) {
        for (ItemType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return FINISHED_GOOD;
    }
}
