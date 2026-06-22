package com.maersk.wms.masterdata.domain;

/**
 * UOM type enumeration.
 */
public enum UomType {
    EACH("EA", "Each"),
    CASE("CS", "Case"),
    PACK("PK", "Pack"),
    INNER_PACK("IP", "Inner Pack"),
    PALLET("PL", "Pallet"),
    CARTON("CT", "Carton"),
    BOX("BX", "Box"),
    BAG("BG", "Bag"),
    ROLL("RL", "Roll"),
    BUNDLE("BD", "Bundle"),
    KIT("KT", "Kit"),
    WEIGHT("WT", "Weight-based"),
    VOLUME("VL", "Volume-based"),
    LENGTH("LN", "Length-based");

    private final String code;
    private final String description;

    UomType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static UomType fromCode(String code) {
        for (UomType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return EACH;
    }
}
