package com.maersk.wms.inbound.domain.operations_service;

/**
 * Status progression for Crossdock operations.
 */
public enum CrossdockStatus {
    PENDING("0", "Pending"),
    ALLOCATED("1", "Allocated"),
    RELEASED("2", "Released"),
    PICKED("3", "Picked"),
    STAGED("4", "Staged"),
    LOADED("5", "Loaded"),
    SHIPPED("6", "Shipped"),
    CANCELLED("9", "Cancelled");

    private final String code;
    private final String label;

    CrossdockStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }

    public static CrossdockStatus fromCode(String code) {
        for (CrossdockStatus status : values()) {
            if (status.code.equals(code)) return status;
        }
        throw new IllegalArgumentException("Unknown crossdock status: " + code);
    }

    public boolean isTerminal() {
        return this == SHIPPED || this == CANCELLED;
    }

    public boolean canBePicked() {
        return this == RELEASED;
    }

    public boolean canBeShipped() {
        return this == LOADED;
    }
}
