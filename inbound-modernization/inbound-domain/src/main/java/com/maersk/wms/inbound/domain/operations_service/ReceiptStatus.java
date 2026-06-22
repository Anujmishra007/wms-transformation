package com.maersk.wms.inbound.domain.operations_service;

/**
 * Status progression for Receipt in Operations subdomain.
 */
public enum ReceiptStatus {
    OPEN("0", "Open"),
    ARRIVED("1", "Arrived"),
    IN_PROGRESS("2", "In Progress"),
    RECEIVED("3", "Received"),
    INSPECTION("4", "Inspection"),
    PUTAWAY_IN_PROGRESS("5", "Putaway In Progress"),
    PUTAWAY_COMPLETE("6", "Putaway Complete"),
    CLOSED("9", "Closed"),
    CANCELLED("X", "Cancelled");

    private final String code;
    private final String description;

    ReceiptStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ReceiptStatus fromCode(String code) {
        for (ReceiptStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown receipt status: " + code);
    }

    public boolean isTerminal() {
        return this == CLOSED || this == CANCELLED;
    }

    public boolean canReceive() {
        return this == OPEN || this == ARRIVED || this == IN_PROGRESS;
    }
}
