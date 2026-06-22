package com.maersk.wms.picking.domain.progression_service.model;

/**
 * Types of pick detail progression events.
 */
public enum ProgressionEventType {
    CREATED("CRT", "Task Created"),
    RELEASED("REL", "Task Released"),
    ASSIGNED("ASN", "Task Assigned"),
    STARTED("STR", "Task Started"),
    PARTIAL_PICK("PRT", "Partial Pick"),
    COMPLETED("CMP", "Task Completed"),
    SHORT_PICK("SHT", "Short Pick"),
    SKIPPED("SKP", "Task Skipped"),
    REASSIGNED("RAS", "Task Reassigned"),
    SUSPENDED("SUS", "Task Suspended"),
    RESUMED("RSM", "Task Resumed"),
    CANCELLED("CNL", "Task Cancelled"),
    QUANTITY_ADJUSTED("QTY", "Quantity Adjusted"),
    LOCATION_CHANGED("LOC", "Location Changed"),
    STATUS_CHANGE("STC", "Status Changed"),
    QUANTITY_CHANGE("QTC", "Quantity Changed"),
    LOCATION_CHANGE("LCC", "Location Changed"),
    LPN_CHANGE("LPN", "LPN Changed"),
    PICK_CONFIRMED("PCK", "Pick Confirmed"),
    ASSIGNMENT_CHANGE("ASC", "Assignment Changed");

    private final String code;
    private final String description;

    ProgressionEventType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ProgressionEventType fromCode(String code) {
        for (ProgressionEventType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown progression event type: " + code);
    }
}
