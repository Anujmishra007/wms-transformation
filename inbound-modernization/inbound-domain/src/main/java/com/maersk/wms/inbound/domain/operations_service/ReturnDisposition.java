package com.maersk.wms.inbound.domain.operations_service;

/**
 * Disposition options for returned items.
 */
public enum ReturnDisposition {
    RESTOCK("RS", "Restock", true, "SELLABLE"),
    REFURBISH("RF", "Refurbish", true, "REFURB"),
    RETURN_TO_VENDOR("RTV", "Return to Vendor", false, "RTV"),
    DESTROY("DST", "Destroy", false, "DESTROY"),
    DONATE("DON", "Donate", false, "DONATE"),
    DISCOUNT("DSC", "Discount Sale", true, "DISCOUNT"),
    SCRAP("SCR", "Scrap", false, "SCRAP"),
    QUALITY_HOLD("QH", "Quality Hold", false, "QA_HOLD");

    private final String code;
    private final String label;
    private final boolean refundable;
    private final String targetZone;

    ReturnDisposition(String code, String label, boolean refundable, String targetZone) {
        this.code = code;
        this.label = label;
        this.refundable = refundable;
        this.targetZone = targetZone;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }
    public boolean isRefundable() { return refundable; }
    public String getTargetZone() { return targetZone; }

    public static ReturnDisposition fromCode(String code) {
        for (ReturnDisposition d : values()) {
            if (d.code.equals(code)) return d;
        }
        throw new IllegalArgumentException("Unknown disposition: " + code);
    }

    public boolean returnsToInventory() {
        return this == RESTOCK || this == REFURBISH || this == DISCOUNT;
    }
}
