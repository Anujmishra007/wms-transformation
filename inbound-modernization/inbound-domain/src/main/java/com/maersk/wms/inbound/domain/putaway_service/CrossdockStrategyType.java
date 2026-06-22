package com.maersk.wms.inbound.domain.putaway_service;

/**
 * Types of Crossdock Strategies in putaway-service subdomain.
 */
public enum CrossdockStrategyType {
    PLANNED("PL", "Planned Crossdock", "Pre-allocated based on inbound/outbound match"),
    OPPORTUNISTIC("OP", "Opportunistic Crossdock", "Real-time match on receipt"),
    FLOW_THROUGH("FT", "Flow-Through", "Continuous movement, minimal staging"),
    MERGE_IN_TRANSIT("MIT", "Merge-in-Transit", "Combine shipments mid-route"),
    RETAIL_ALLOCATION("RA", "Retail Allocation", "Store-level allocation on receipt"),
    WAVE_BASED("WB", "Wave-Based", "Match to active wave demand");

    private final String code;
    private final String label;
    private final String description;

    CrossdockStrategyType(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }
    public String getDescription() { return description; }

    public static CrossdockStrategyType fromCode(String code) {
        for (CrossdockStrategyType type : values()) {
            if (type.code.equals(code)) return type;
        }
        return OPPORTUNISTIC;
    }

    public boolean requiresPreAllocation() {
        return this == PLANNED || this == RETAIL_ALLOCATION;
    }

    public boolean supportsOpportunisticMatch() {
        return this == OPPORTUNISTIC || this == WAVE_BASED;
    }
}
