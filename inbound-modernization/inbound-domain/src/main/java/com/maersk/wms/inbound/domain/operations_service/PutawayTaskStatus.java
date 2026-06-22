package com.maersk.wms.inbound.domain.operations_service;

/**
 * Status for Putaway Tasks in Operations subdomain.
 */
public enum PutawayTaskStatus {
    PENDING("0", "Pending"),
    ASSIGNED("1", "Assigned"),
    IN_PROGRESS("2", "In Progress"),
    COMPLETED("3", "Completed"),
    SHORT("4", "Short Putaway"),
    CANCELLED("9", "Cancelled"),
    ON_HOLD("H", "On Hold");

    private final String code;
    private final String label;

    PutawayTaskStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }

    public static PutawayTaskStatus fromCode(String code) {
        for (PutawayTaskStatus status : values()) {
            if (status.code.equals(code)) return status;
        }
        throw new IllegalArgumentException("Unknown putaway task status: " + code);
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED;
    }

    public boolean canBeAssigned() {
        return this == PENDING;
    }

    public boolean canBeStarted() {
        return this == ASSIGNED;
    }
}
