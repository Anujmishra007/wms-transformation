package com.maersk.wms.inventory.domain;

/**
 * Inventory status enumeration.
 * Maps to legacy status codes in LOTxLOCxID.STATUS
 */
public enum InventoryStatus {
    /** Available for allocation and picking */
    AVAILABLE(0, "Available"),

    /** On hold - not available for allocation */
    HOLD(1, "Hold"),

    /** Damaged inventory */
    DAMAGED(2, "Damaged"),

    /** Quality control inspection */
    QC_INSPECTION(3, "QC Inspection"),

    /** Reserved for specific order */
    RESERVED(4, "Reserved"),

    /** In transit between locations */
    IN_TRANSIT(5, "In Transit"),

    /** Pending putaway */
    PENDING_PUTAWAY(6, "Pending Putaway"),

    /** Quarantine */
    QUARANTINE(7, "Quarantine"),

    /** Returns processing */
    RETURNS(8, "Returns"),

    /** Expired */
    EXPIRED(9, "Expired");

    private final int legacyCode;
    private final String displayName;

    InventoryStatus(int legacyCode, String displayName) {
        this.legacyCode = legacyCode;
        this.displayName = displayName;
    }

    public int getLegacyCode() {
        return legacyCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static InventoryStatus fromLegacyCode(int code) {
        for (InventoryStatus status : values()) {
            if (status.legacyCode == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown legacy status code: " + code);
    }

    /**
     * Check if status allows allocation.
     */
    public boolean isAllocatable() {
        return this == AVAILABLE;
    }

    /**
     * Check if status is a hold status.
     */
    public boolean isHoldStatus() {
        return this == HOLD || this == DAMAGED || this == QC_INSPECTION
               || this == QUARANTINE || this == EXPIRED;
    }
}
