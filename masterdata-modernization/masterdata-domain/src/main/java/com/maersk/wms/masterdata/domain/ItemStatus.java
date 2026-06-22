package com.maersk.wms.masterdata.domain;

/**
 * Item status enumeration.
 */
public enum ItemStatus {
    ACTIVE("A", "Active"),
    INACTIVE("I", "Inactive"),
    DISCONTINUED("D", "Discontinued"),
    PENDING("P", "Pending Approval"),
    BLOCKED("B", "Blocked");

    private final String code;
    private final String description;

    ItemStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ItemStatus fromCode(String code) {
        for (ItemStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return ACTIVE;
    }
}
