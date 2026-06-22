package com.maersk.wms.inbound.domain.operations_service;

/**
 * Types of Crossdock operations in Operations subdomain.
 */
public enum CrossdockType {
    PLANNED("PL", "Planned Crossdock", true),
    OPPORTUNISTIC("OP", "Opportunistic Crossdock", false),
    FLOW_THROUGH("FT", "Flow-Through", true),
    MERGE_IN_TRANSIT("MIT", "Merge-in-Transit", true),
    PRE_ALLOCATED("PA", "Pre-Allocated", true),
    RETAIL("RT", "Retail Crossdock", true);

    private final String code;
    private final String label;
    private final boolean requiresPreAllocation;

    CrossdockType(String code, String label, boolean requiresPreAllocation) {
        this.code = code;
        this.label = label;
        this.requiresPreAllocation = requiresPreAllocation;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }
    public boolean isRequiresPreAllocation() { return requiresPreAllocation; }

    public static CrossdockType fromCode(String code) {
        for (CrossdockType type : values()) {
            if (type.code.equals(code)) return type;
        }
        return OPPORTUNISTIC;
    }

    public boolean allowsOpportunisticMatch() {
        return this == OPPORTUNISTIC;
    }

    public boolean requiresWave() {
        return this == PLANNED || this == PRE_ALLOCATED;
    }
}
