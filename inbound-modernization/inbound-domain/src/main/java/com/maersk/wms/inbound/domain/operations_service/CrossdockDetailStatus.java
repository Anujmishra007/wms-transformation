package com.maersk.wms.inbound.domain.operations_service;

/**
 * Status for Crossdock Detail lines.
 */
public enum CrossdockDetailStatus {
    PENDING("0", "Pending"),
    ALLOCATED("1", "Allocated"),
    PICKED("2", "Picked"),
    STAGED("3", "Staged"),
    SHIPPED("4", "Shipped"),
    SHORT("S", "Short"),
    CANCELLED("9", "Cancelled");

    private final String code;
    private final String label;

    CrossdockDetailStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }

    public static CrossdockDetailStatus fromCode(String code) {
        for (CrossdockDetailStatus status : values()) {
            if (status.code.equals(code)) return status;
        }
        throw new IllegalArgumentException("Unknown crossdock detail status: " + code);
    }
}
