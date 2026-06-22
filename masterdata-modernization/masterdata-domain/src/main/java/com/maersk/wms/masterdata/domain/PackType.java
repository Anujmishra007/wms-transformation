package com.maersk.wms.masterdata.domain;

/**
 * Pack type enumeration.
 */
public enum PackType {
    CARTON("CTN", "Carton/Box"),
    PALLET("PLT", "Pallet"),
    TOTE("TOT", "Tote/Bin"),
    GAYLORD("GYL", "Gaylord"),
    BAG("BAG", "Bag"),
    DRUM("DRM", "Drum"),
    CRATE("CRT", "Crate"),
    ENVELOPE("ENV", "Envelope"),
    TUBE("TUB", "Tube"),
    CUSTOM("CST", "Custom Container");

    private final String code;
    private final String description;

    PackType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static PackType fromCode(String code) {
        for (PackType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return CARTON;
    }
}
