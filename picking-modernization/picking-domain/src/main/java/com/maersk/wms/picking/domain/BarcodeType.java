package com.maersk.wms.picking.domain;

/**
 * Barcode type enumeration for FN839 decode operations.
 */
public enum BarcodeType {
    /** Location barcode (e.g., A01-02-03) */
    LOCATION("LOC", "Location"),

    /** SKU/Item barcode */
    SKU("SKU", "SKU/Item"),

    /** License Plate Number barcode */
    LPN("LPN", "License Plate"),

    /** Lot number barcode */
    LOT("LOT", "Lot Number"),

    /** Serial number barcode */
    SERIAL("SER", "Serial Number"),

    /** Container/Cart barcode */
    CONTAINER("CNT", "Container"),

    /** Equipment barcode */
    EQUIPMENT("EQP", "Equipment"),

    /** User badge barcode */
    USER("USR", "User Badge"),

    /** GS1-128 composite barcode */
    GS1_128("GS1", "GS1-128"),

    /** UPC barcode */
    UPC("UPC", "UPC"),

    /** QR code */
    QR("QR", "QR Code"),

    /** Unknown type */
    UNKNOWN("UNK", "Unknown");

    private final String code;
    private final String description;

    BarcodeType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static BarcodeType fromCode(String code) {
        for (BarcodeType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
