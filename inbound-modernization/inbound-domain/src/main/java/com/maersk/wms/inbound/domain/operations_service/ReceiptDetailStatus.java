package com.maersk.wms.inbound.domain.operations_service;

/**
 * Status for Receipt Detail lines in Operations subdomain.
 */
public enum ReceiptDetailStatus {
    OPEN("0", "Open"),
    PARTIAL("1", "Partial"),
    RECEIVED("2", "Received"),
    OVER_RECEIVED("3", "Over Received"),
    INSPECTION_REQUIRED("4", "Inspection Required"),
    INSPECTED("5", "Inspected"),
    READY_FOR_PUTAWAY("6", "Ready for Putaway"),
    PUTAWAY_IN_PROGRESS("7", "Putaway In Progress"),
    PUTAWAY_COMPLETE("8", "Putaway Complete"),
    CLOSED("9", "Closed"),
    CANCELLED("X", "Cancelled"),
    REJECTED("R", "Rejected");

    private final String code;
    private final String description;

    ReceiptDetailStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ReceiptDetailStatus fromCode(String code) {
        for (ReceiptDetailStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown receipt detail status: " + code);
    }

    public boolean isReceived() {
        return this == RECEIVED || this == OVER_RECEIVED || this == INSPECTED ||
               this == READY_FOR_PUTAWAY || this == PUTAWAY_IN_PROGRESS || this == PUTAWAY_COMPLETE;
    }

    public boolean isPutAway() {
        return this == PUTAWAY_COMPLETE || this == CLOSED;
    }
}
