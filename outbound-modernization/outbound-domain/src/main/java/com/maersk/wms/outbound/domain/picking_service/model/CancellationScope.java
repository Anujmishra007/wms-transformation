package com.maersk.wms.outbound.domain.picking_service.model;

/**
 * Scope of pick cancellation.
 */
public enum CancellationScope {
    PICK_DETAIL("PD", "Single Pick Detail"),
    PICK_HEADER("PH", "Entire Pick Header"),
    ORDER("ORD", "Entire Order"),
    WAVE("WAV", "Entire Wave");

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
        return PICK_DETAIL;
    }
}
