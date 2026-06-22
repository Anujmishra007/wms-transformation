package com.maersk.wms.inbound.domain.operations_service;

/**
 * Reasons for return.
 */
public enum ReturnReason {
    CHANGED_MIND("CM", "Changed Mind", true),
    NOT_AS_DESCRIBED("NAD", "Not As Described", true),
    WRONG_SIZE("WS", "Wrong Size", true),
    DEFECTIVE("DEF", "Defective", true),
    DAMAGED("DMG", "Damaged", true),
    MISSING_PARTS("MP", "Missing Parts", true),
    LATE_DELIVERY("LD", "Late Delivery", true),
    WRONG_ITEM_SENT("WIS", "Wrong Item Sent", true),
    OTHER("OTH", "Other", true);

    private final String code;
    private final String description;
    private final boolean refundEligible;

    ReturnReason(String code, String description, boolean refundEligible) {
        this.code = code;
        this.description = description;
        this.refundEligible = refundEligible;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }
    public boolean isRefundEligible() { return refundEligible; }

    public static ReturnReason fromCode(String code) {
        for (ReturnReason reason : values()) {
            if (reason.code.equals(code)) return reason;
        }
        return OTHER;
    }
}
