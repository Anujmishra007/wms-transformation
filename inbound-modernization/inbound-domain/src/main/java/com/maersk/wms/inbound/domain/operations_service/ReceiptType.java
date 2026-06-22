package com.maersk.wms.inbound.domain.operations_service;

/**
 * Types of receipts in Operations subdomain.
 */
public enum ReceiptType {
    NORMAL("N", "Normal Receipt"),
    RETURN("R", "Return Receipt"),
    TRANSFER("T", "Transfer Receipt"),
    CROSS_DOCK("X", "Cross-Dock Receipt"),
    PRODUCTION("P", "Production Receipt");

    private final String code;
    private final String description;

    ReceiptType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ReceiptType fromCode(String code) {
        for (ReceiptType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return NORMAL;
    }

    public boolean requiresInspection() {
        return this == RETURN;
    }

    public boolean skipsPutaway() {
        return this == CROSS_DOCK;
    }
}
