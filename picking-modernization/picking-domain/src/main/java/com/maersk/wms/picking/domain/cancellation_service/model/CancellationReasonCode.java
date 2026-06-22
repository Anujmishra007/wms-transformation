package com.maersk.wms.picking.domain.cancellation_service.model;

/**
 * Pick cancellation reason codes.
 */
public enum CancellationReasonCode {
    CUSTOMER_REQUEST("CUS", "Customer Request", true),
    ORDER_CANCELLED("ORD", "Order Cancelled", true),
    INVENTORY_ISSUE("INV", "Inventory Issue", false),
    ALLOCATION_ERROR("ALC", "Allocation Error", false),
    DUPLICATE_TASK("DUP", "Duplicate Task", false),
    SYSTEM_ERROR("SYS", "System Error", false),
    WAVE_CANCELLED("WAV", "Wave Cancelled", true),
    PRIORITY_CHANGE("PRI", "Priority Change", false),
    REASSIGNMENT("RAS", "Task Reassignment", false),
    EQUIPMENT_FAILURE("EQP", "Equipment Failure", false),
    OTHER("OTH", "Other", false);

    private final String code;
    private final String description;
    private final boolean requiresApproval;

    CancellationReasonCode(String code, String description, boolean requiresApproval) {
        this.code = code;
        this.description = description;
        this.requiresApproval = requiresApproval;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequiresApproval() {
        return requiresApproval;
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
