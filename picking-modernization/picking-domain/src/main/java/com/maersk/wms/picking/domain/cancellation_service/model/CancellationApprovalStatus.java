package com.maersk.wms.picking.domain.cancellation_service.model;

/**
 * Cancellation approval status.
 */
public enum CancellationApprovalStatus {
    NOT_REQUIRED("N", "Approval Not Required"),
    PENDING("P", "Pending Approval"),
    APPROVED("A", "Approved"),
    REJECTED("R", "Rejected");

    private final String code;
    private final String description;

    CancellationApprovalStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static CancellationApprovalStatus fromCode(String code) {
        for (CancellationApprovalStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return NOT_REQUIRED;
    }
}
