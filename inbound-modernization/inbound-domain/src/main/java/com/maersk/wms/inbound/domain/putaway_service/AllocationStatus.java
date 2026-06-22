package com.maersk.wms.inbound.domain.putaway_service;

/**
 * Status for Location Allocation in putaway-service subdomain.
 */
public enum AllocationStatus {
    AVAILABLE("0", "Available"),
    RESERVED("1", "Reserved"),
    CONFIRMED("2", "Confirmed"),
    RELEASED("3", "Released"),
    EXPIRED("4", "Expired"),
    REJECTED("5", "Rejected"),
    LOCKED("L", "Locked");

    private final String code;
    private final String label;

    AllocationStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }

    public static AllocationStatus fromCode(String code) {
        for (AllocationStatus status : values()) {
            if (status.code.equals(code)) return status;
        }
        throw new IllegalArgumentException("Unknown allocation status: " + code);
    }

    public boolean canBeAllocated() {
        return this == AVAILABLE;
    }

    public boolean isActive() {
        return this == RESERVED || this == CONFIRMED;
    }
}
