package com.maersk.wms.outbound.domain.picking_service.model;

/**
 * Pick cancellation reason codes.
 */
public enum CancellationReasonCode {
    CUSTOMER_REQUEST("CUS", "Customer Request"),
    ORDER_CANCELLED("ORD", "Order Cancelled"),
    INVENTORY_ISSUE("INV", "Inventory Issue"),
    ALLOCATION_ERROR("ALC", "Allocation Error"),
    DUPLICATE("DUP", "Duplicate Pick"),
    SYSTEM_ERROR("SYS", "System Error"),
    WAVE_CANCELLED("WAV", "Wave Cancelled"),
    PRIORITY_CHANGE("PRI", "Priority Change"),
    OTHER("OTH", "Other");

    private final String code;
    private final String description;

    CancellationReasonCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static CancellationReasonCode fromCode(String code) {
        for (CancellationReasonCode reason : values()) {
            if (reason.code.equals(code)) {
                return reason;
            }
        }
        return OTHER;
    }
}
