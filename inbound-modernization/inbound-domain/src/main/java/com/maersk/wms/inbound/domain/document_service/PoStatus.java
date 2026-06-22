package com.maersk.wms.inbound.domain.document_service;

/**
 * Status progression for Purchase Order.
 */
public enum PoStatus {
    DRAFT("0", "Draft", "PO created but not yet released"),
    OPEN("1", "Open", "PO released and open for receiving"),
    PARTIAL("2", "Partially Received", "Some lines received"),
    RECEIVED("3", "Fully Received", "All lines received"),
    CLOSED("9", "Closed", "PO closed and finalized"),
    CANCELLED("X", "Cancelled", "PO cancelled");

    private final String code;
    private final String label;
    private final String description;

    PoStatus(String code, String label, String description) {
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

    public static PoStatus fromCode(String code) {
        for (PoStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown PO status code: " + code);
    }

    public boolean isOpen() {
        return this == DRAFT || this == OPEN || this == PARTIAL;
    }

    public boolean isTerminal() {
        return this == CLOSED || this == CANCELLED;
    }
}
