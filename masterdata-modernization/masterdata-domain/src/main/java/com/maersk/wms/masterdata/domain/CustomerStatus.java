package com.maersk.wms.masterdata.domain;

/**
 * Customer status enumeration.
 */
public enum CustomerStatus {
    ACTIVE("A", "Active"),
    INACTIVE("I", "Inactive"),
    SUSPENDED("S", "Suspended"),
    PENDING("P", "Pending Approval"),
    BLOCKED("B", "Blocked");

    private final String code;
    private final String description;

    CustomerStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static CustomerStatus fromCode(String code) {
        for (CustomerStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return ACTIVE;
    }
}
