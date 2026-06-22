package com.maersk.wms.picking.domain.cancellation_service.model;

/**
 * Scope of pick cancellation.
 */
public enum CancellationScope {
    SINGLE_TASK("TASK", "Single Pick Task"),
    PICK_LIST("LIST", "Entire Pick List"),
    ORDER("ORDER", "All Picks for Order"),
    WAVE("WAVE", "All Picks for Wave"),
    USER_SESSION("USER", "All Picks in User Session");

    private final String code;
    private final String description;

    CancellationScope(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static CancellationScope fromCode(String code) {
        for (CancellationScope scope : values()) {
            if (scope.code.equals(code)) {
                return scope;
            }
        }
        return SINGLE_TASK;
    }
}
