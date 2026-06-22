package com.maersk.wms.inbound.domain.document_service;

/**
 * Status progression for PO Detail lines.
 */
public enum PoDetailStatus {
    OPEN("0", "Open"),
    PARTIAL("1", "Partially Received"),
    RECEIVED("2", "Fully Received"),
    OVER_RECEIVED("3", "Over Received"),
    CLOSED("9", "Closed"),
    CANCELLED("X", "Cancelled");

    private final String code;
    private final String label;

    PoDetailStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static PoDetailStatus fromCode(String code) {
        for (PoDetailStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown PO detail status code: " + code);
    }
}
