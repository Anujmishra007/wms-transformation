package com.maersk.wms.picking.domain.shorts_service.model;

/**
 * Short pick resolution status.
 */
public enum ShortResolutionStatus {
    PENDING("P", "Pending Resolution"),
    VERIFIED("V", "Verified"),
    PENDING_APPROVAL("A", "Pending Supervisor Approval"),
    APPROVED("Y", "Approved"),
    REJECTED("N", "Rejected"),
    IN_PROGRESS("I", "Resolution In Progress"),
    RESOLVED("R", "Resolved"),
    CANCELLED("X", "Cancelled"),
    ESCALATED("E", "Escalated");

    private final String code;
    private final String description;

    ShortResolutionStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ShortResolutionStatus fromCode(String code) {
        for (ShortResolutionStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return PENDING;
    }
}
