package com.maersk.wms.task.domain.enums;

/**
 * Work group status.
 */
public enum WorkGroupStatus {
    ACTIVE("A", "Active", "Work group is active and accepting tasks"),
    INACTIVE("I", "Inactive", "Work group is inactive"),
    SUSPENDED("S", "Suspended", "Work group is temporarily suspended"),
    FULL("F", "Full", "Work group is at capacity");

    private final String code;
    private final String displayName;
    private final String description;

    WorkGroupStatus(String code, String displayName, String description) {
        this.code = code;
        this.displayName = displayName;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static WorkGroupStatus fromCode(String code) {
        for (WorkGroupStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown work group status code: " + code);
    }
}
