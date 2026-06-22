package com.maersk.wms.inbound.domain.operations_service;

/**
 * Status for Return Detail lines.
 */
public enum ReturnDetailStatus {
    OPEN("0", "Open"),
    RECEIVED("1", "Received"),
    INSPECTION_PENDING("2", "Inspection Pending"),
    INSPECTED("3", "Inspected"),
    DISPOSITION_PENDING("4", "Disposition Pending"),
    DISPOSITIONED("5", "Dispositioned"),
    PUTAWAY_PENDING("6", "Putaway Pending"),
    PUTAWAY_COMPLETE("7", "Putaway Complete"),
    CLOSED("9", "Closed"),
    REJECTED("R", "Rejected");

    private final String code;
    private final String label;

    ReturnDetailStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }

    public static ReturnDetailStatus fromCode(String code) {
        for (ReturnDetailStatus status : values()) {
            if (status.code.equals(code)) return status;
        }
        throw new IllegalArgumentException("Unknown return detail status: " + code);
    }
}
