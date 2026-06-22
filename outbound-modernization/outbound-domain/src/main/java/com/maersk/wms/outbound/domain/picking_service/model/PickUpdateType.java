package com.maersk.wms.outbound.domain.picking_service.model;

/**
 * Types of pick detail updates.
 */
public enum PickUpdateType {
    CREATED("CRT", "Pick Created"),
    RELEASED("REL", "Pick Released"),
    ASSIGNED("ASN", "Pick Assigned"),
    STARTED("STR", "Pick Started"),
    PARTIAL_PICK("PRT", "Partial Pick"),
    COMPLETED("CMP", "Pick Completed"),
    SHORT_PICK("SHT", "Short Pick"),
    CANCELLED("CNL", "Pick Cancelled"),
    QUANTITY_CHANGE("QTY", "Quantity Changed"),
    STATUS_CHANGE("STS", "Status Changed"),
    LOCATION_CHANGE("LOC", "Location Changed"),
    REASSIGNED("RAS", "Reassigned");

    private final String code;
    private final String description;

    PickUpdateType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static PickUpdateType fromCode(String code) {
        for (PickUpdateType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown pick update type: " + code);
    }
}
