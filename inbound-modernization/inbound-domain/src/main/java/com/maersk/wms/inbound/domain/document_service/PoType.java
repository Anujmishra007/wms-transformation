package com.maersk.wms.inbound.domain.document_service;

/**
 * Types of Purchase Orders.
 */
public enum PoType {
    STANDARD("S", "Standard PO", "Regular purchase order"),
    BLANKET("B", "Blanket PO", "Long-term agreement with multiple releases"),
    RELEASE("R", "Release", "Release against blanket PO"),
    EMERGENCY("E", "Emergency PO", "Urgent/expedited order"),
    TRANSFER("T", "Transfer Order", "Inter-warehouse transfer"),
    RETURN_TO_VENDOR("RTV", "Return to Vendor", "Sending goods back to vendor"),
    CONSIGNMENT("C", "Consignment", "Vendor-owned inventory");

    private final String code;
    private final String label;
    private final String description;

    PoType(String code, String label, String description) {
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

    public static PoType fromCode(String code) {
        for (PoType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return STANDARD;
    }
}
