package com.maersk.wms.outbound.domain.picking_service.model;

/**
 * Short pick resolution status.
 */
public enum ShortResolutionStatus {
    PENDING("P", "Pending Resolution"),
    REALLOCATED("R", "Reallocated"),
    BACKORDERED("B", "Backordered"),
    CANCELLED("X", "Cancelled"),
    RESOLVED("C", "Resolved");

    private final String code;
    private final String description;

    ShortResolutionStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ShortResolutionStatus fromCode(String code) {
        for (ShortResolutionStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return PENDING;
    }
}
