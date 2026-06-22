package com.maersk.wms.inbound.domain.putaway_service;

/**
 * Types of putaway strategies.
 */
public enum PutawayStrategyType {
    /**
     * System selects location automatically.
     */
    DIRECTED("D", "Directed Putaway"),

    /**
     * User selects location.
     */
    USER_DIRECTED("U", "User Directed"),

    /**
     * Put in fixed location for SKU.
     */
    FIXED_LOCATION("F", "Fixed Location"),

    /**
     * Random storage in allowed zones.
     */
    RANDOM("R", "Random Storage"),

    /**
     * Put in location with existing same SKU.
     */
    CONSOLIDATE("C", "Consolidate"),

    /**
     * For cross-dock items (minimal storage).
     */
    CROSS_DOCK("X", "Cross-Dock"),

    /**
     * For returns based on disposition.
     */
    RETURN_DISPOSITION("RD", "Return Disposition");

    private final String code;
    private final String description;

    PutawayStrategyType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static PutawayStrategyType fromCode(String code) {
        for (PutawayStrategyType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return DIRECTED;
    }
}
