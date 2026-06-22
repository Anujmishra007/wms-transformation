package com.maersk.wms.masterdata.domain;

/**
 * Carrier status enumeration.
 */
public enum CarrierStatus {
    ACTIVE("A", "Active"),
    INACTIVE("I", "Inactive"),
    SUSPENDED("S", "Suspended"),
    PENDING("P", "Pending Setup");

    private final String code;
    private final String description;

    CarrierStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static CarrierStatus fromCode(String code) {
        for (CarrierStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return ACTIVE;
    }
}
