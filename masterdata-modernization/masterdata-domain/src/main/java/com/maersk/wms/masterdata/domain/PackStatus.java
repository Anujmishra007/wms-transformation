package com.maersk.wms.masterdata.domain;

/**
 * Pack status enumeration.
 */
public enum PackStatus {
    ACTIVE("A", "Active"),
    INACTIVE("I", "Inactive"),
    DISCONTINUED("D", "Discontinued");

    private final String code;
    private final String description;

    PackStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static PackStatus fromCode(String code) {
        for (PackStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return ACTIVE;
    }
}
