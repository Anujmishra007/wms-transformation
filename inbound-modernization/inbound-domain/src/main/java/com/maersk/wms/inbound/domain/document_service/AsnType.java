package com.maersk.wms.inbound.domain.document_service;

/**
 * Types of ASN in Document subdomain.
 */
public enum AsnType {
    STANDARD("S", "Standard ASN", "Regular advance ship notice"),
    TRANSFER("T", "Transfer ASN", "Inter-warehouse transfer"),
    RETURN("R", "Return ASN", "Customer return notification"),
    CROSS_DOCK("X", "Cross-Dock ASN", "Cross-docking shipment"),
    EDI("E", "EDI ASN", "Received via EDI 856");

    private final String code;
    private final String label;
    private final String description;

    AsnType(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public static AsnType fromCode(String code) {
        for (AsnType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return STANDARD;
    }
}
