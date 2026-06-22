package com.maersk.wms.picking.domain;

/**
 * Pick type enumeration - differentiates picking methods.
 */
public enum PickType {
    /** Standard pick from inventory location */
    STANDARD("STD", "Standard Pick"),

    /** Pick from forward pick location */
    FORWARD_PICK("FWD", "Forward Pick"),

    /** Pick requiring replenishment */
    REPLEN_PICK("RPL", "Replen Pick"),

    /** Direct allocation pick */
    DIRECT_ALLOC("DIR", "Direct Allocation"),

    /** Cross-dock pick */
    CROSS_DOCK("XDK", "Cross Dock"),

    /** VAS (Value Added Service) pick */
    VAS_PICK("VAS", "VAS Pick"),

    /** Returns processing pick */
    RETURNS("RET", "Returns Pick");

    private final String code;
    private final String description;

    PickType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static PickType fromCode(String code) {
        for (PickType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return STANDARD; // Default
    }
}
