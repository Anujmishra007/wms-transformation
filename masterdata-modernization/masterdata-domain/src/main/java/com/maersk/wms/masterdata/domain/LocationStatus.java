package com.maersk.wms.masterdata.domain;

/**
 * Location status enumeration.
 */
public enum LocationStatus {
    AVAILABLE("A", "Available"),
    OCCUPIED("O", "Occupied"),
    FULL("F", "Full"),
    LOCKED("L", "Locked"),
    DISABLED("D", "Disabled"),
    MAINTENANCE("M", "Under Maintenance"),
    RESERVED("R", "Reserved");

    private final String code;
    private final String description;

    LocationStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static LocationStatus fromCode(String code) {
        for (LocationStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return AVAILABLE;
    }
}
