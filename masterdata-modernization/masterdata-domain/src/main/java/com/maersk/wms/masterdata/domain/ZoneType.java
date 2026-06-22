package com.maersk.wms.masterdata.domain;

/**
 * Zone type enumeration.
 */
public enum ZoneType {
    STORAGE("STG", "Storage Zone"),
    PICKING("PCK", "Picking Zone"),
    RECEIVING("RCV", "Receiving Zone"),
    SHIPPING("SHP", "Shipping Zone"),
    STAGING("STN", "Staging Zone"),
    QC("QC", "Quality Control Zone"),
    RETURNS("RTN", "Returns Zone"),
    CROSSDOCK("XDK", "Cross Dock Zone"),
    PRODUCTION("PRD", "Production Zone"),
    HAZMAT("HZM", "Hazmat Zone"),
    REFRIGERATED("REF", "Refrigerated Zone"),
    FREEZER("FRZ", "Freezer Zone"),
    SECURE("SEC", "Secure Zone"),
    BULK("BLK", "Bulk Storage Zone");

    private final String code;
    private final String description;

    ZoneType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ZoneType fromCode(String code) {
        for (ZoneType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return STORAGE;
    }
}
