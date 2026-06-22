package com.maersk.wms.inbound.domain.document_service;

/**
 * Status progression for OSD reports.
 */
public enum OsdStatus {
    OPEN("0", "Open", "Report created"),
    UNDER_REVIEW("1", "Under Review", "Being reviewed"),
    PENDING_REVIEW("1A", "Pending Review", "Submitted for review"),
    PENDING_VENDOR("2", "Pending Vendor", "Awaiting vendor response"),
    APPROVED("2A", "Approved", "Report approved"),
    CLAIM_FILED("3", "Claim Filed", "Claim submitted to carrier/vendor"),
    CLAIM_PENDING("3A", "Claim Pending", "Claim initiated"),
    REJECTED("R", "Rejected", "Report rejected"),
    RESOLVED("9", "Resolved", "Issue resolved"),
    CLOSED("C", "Closed", "Report closed"),
    CANCELLED("X", "Cancelled", "Report cancelled");

    private final String code;
    private final String label;
    private final String description;

    OsdStatus(String code, String label, String description) {
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

    public static OsdStatus fromCode(String code) {
        for (OsdStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown OSD status: " + code);
    }

    public boolean isTerminal() {
        return this == RESOLVED || this == CLOSED || this == CANCELLED;
    }
}
