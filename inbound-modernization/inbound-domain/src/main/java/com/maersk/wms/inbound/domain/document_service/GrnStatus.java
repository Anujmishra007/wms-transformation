package com.maersk.wms.inbound.domain.document_service;

/**
 * Status progression for GRN.
 */
public enum GrnStatus {
    DRAFT("0", "Draft", "GRN created, not yet finalized"),
    PENDING_APPROVAL("1", "Pending Approval", "Awaiting approval"),
    APPROVED("2", "Approved", "Approved for posting"),
    POSTED("3", "Posted", "Posted to ERP/financial system"),
    CLOSED("9", "Closed", "GRN closed"),
    CANCELLED("X", "Cancelled", "GRN cancelled");

    private final String code;
    private final String label;
    private final String description;

    GrnStatus(String code, String label, String description) {
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

    public static GrnStatus fromCode(String code) {
        for (GrnStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown GRN status: " + code);
    }

    public boolean isTerminal() {
        return this == CLOSED || this == CANCELLED;
    }
}
