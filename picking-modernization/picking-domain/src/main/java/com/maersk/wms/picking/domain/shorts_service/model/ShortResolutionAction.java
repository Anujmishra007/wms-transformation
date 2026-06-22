package com.maersk.wms.picking.domain.shorts_service.model;

/**
 * Short pick resolution actions.
 */
public enum ShortResolutionAction {
    REALLOCATE("REALLOC", "Reallocate from different location"),
    BACKORDER("BACKORD", "Backorder the quantity"),
    SUBSTITUTE("SUBST", "Substitute with different SKU"),
    CANCEL_LINE("CANCEL", "Cancel the order line"),
    ADJUST_INVENTORY("ADJINV", "Adjust inventory"),
    REPLENISHMENT("REPLEN", "Trigger replenishment"),
    MANUAL_PICK("MANUAL", "Manual pick required"),
    NO_ACTION("NONE", "No action required");

    private final String code;
    private final String description;

    ShortResolutionAction(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ShortResolutionAction fromCode(String code) {
        for (ShortResolutionAction action : values()) {
            if (action.code.equals(code)) {
                return action;
            }
        }
        return NO_ACTION;
    }
}
