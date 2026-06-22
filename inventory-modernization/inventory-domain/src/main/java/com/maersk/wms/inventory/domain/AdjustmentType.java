package com.maersk.wms.inventory.domain;

/**
 * Adjustment type enumeration.
 */
public enum AdjustmentType {
    /** Cycle count adjustment */
    CYCLE_COUNT("CC", "Cycle Count"),

    /** Physical inventory adjustment */
    PHYSICAL_INVENTORY("PI", "Physical Inventory"),

    /** Damage write-off */
    DAMAGE("DMG", "Damage"),

    /** Expiry write-off */
    EXPIRY("EXP", "Expiry"),

    /** Shortage adjustment */
    SHORTAGE("SHT", "Shortage"),

    /** Overage adjustment */
    OVERAGE("OVR", "Overage"),

    /** Returns adjustment */
    RETURNS("RET", "Returns"),

    /** Quality rejection */
    QC_REJECT("QCR", "QC Rejection"),

    /** Manual adjustment */
    MANUAL("MAN", "Manual"),

    /** System correction */
    SYSTEM_CORRECTION("SYS", "System Correction");

    private final String code;
    private final String description;

    AdjustmentType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AdjustmentType fromCode(String code) {
        for (AdjustmentType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown adjustment type code: " + code);
    }

    /**
     * Check if adjustment requires supervisor approval.
     */
    public boolean requiresApproval() {
        return this == DAMAGE || this == MANUAL || this == SYSTEM_CORRECTION;
    }
}
