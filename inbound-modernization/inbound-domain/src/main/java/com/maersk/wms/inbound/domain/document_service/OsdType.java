package com.maersk.wms.inbound.domain.document_service;

/**
 * Types of OSD reports.
 */
public enum OsdType {
    OVERAGE("O", "Overage", "Received more than expected"),
    SHORTAGE("S", "Shortage", "Received less than expected"),
    DAMAGE("D", "Damage", "Items damaged"),
    MIXED("M", "Mixed", "Multiple discrepancy types");

    private final String code;
    private final String label;
    private final String description;

    OsdType(String code, String label, String description) {
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

    public static OsdType fromCode(String code) {
        for (OsdType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return MIXED;
    }
}
