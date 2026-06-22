package com.maersk.wms.inbound.domain.operations_service;

/**
 * Status progression for Trade Return in Operations subdomain.
 */
public enum ReturnStatus {
    OPEN("0", "Open"),
    RECEIVING("1", "Receiving"),
    RECEIVED("2", "Received"),
    INSPECTING("3", "Inspecting"),
    INSPECTED("4", "Inspected"),
    DISPOSITIONING("5", "Dispositioning"),
    DISPOSITIONED("6", "Dispositioned"),
    PUTAWAY_IN_PROGRESS("7", "Putaway In Progress"),
    PUTAWAY_COMPLETE("8", "Putaway Complete"),
    CLOSED("9", "Closed"),
    CANCELLED("X", "Cancelled");

    private final String code;
    private final String label;

    ReturnStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }

    public static ReturnStatus fromCode(String code) {
        for (ReturnStatus status : values()) {
            if (status.code.equals(code)) return status;
        }
        throw new IllegalArgumentException("Unknown return status: " + code);
    }

    public boolean isTerminal() { return this == CLOSED || this == CANCELLED; }
    public boolean canReceive() { return this == OPEN || this == RECEIVING; }
    public boolean canInspect() { return this == RECEIVED || this == INSPECTING; }
}
