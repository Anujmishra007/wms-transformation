package com.maersk.wms.masterdata.domain;

/**
 * Zone status enumeration.
 */
public enum ZoneStatus {
    ACTIVE("A", "Active"),
    INACTIVE("I", "Inactive"),
    MAINTENANCE("M", "Under Maintenance"),
    CLOSED("C", "Closed");

    private final String code;
    private final String description;

    ZoneStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ZoneStatus fromCode(String code) {
        for (ZoneStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return ACTIVE;
    }
}
