package com.maersk.wms.outbound.domain.allocation_service.model;

/**
 * PickSlip status enumeration.
 */
public enum PickSlipStatus {
    CREATED("0", "Created"),
    PRINTED("1", "Printed"),
    IN_PROGRESS("5", "In Progress"),
    COMPLETED("9", "Completed"),
    CANCELLED("X", "Cancelled");

    private final String code;
    private final String description;

    PickSlipStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static PickSlipStatus fromCode(String code) {
        for (PickSlipStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown pick slip status code: " + code);
    }
}
