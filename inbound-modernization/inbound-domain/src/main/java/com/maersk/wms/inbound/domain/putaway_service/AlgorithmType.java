package com.maersk.wms.inbound.domain.putaway_service;

/**
 * Types of Putaway Algorithms in putaway-service subdomain.
 */
public enum AlgorithmType {
    DIRECTED("DIR", "Directed Putaway", "System selects optimal location"),
    FIXED_LOCATION("FIX", "Fixed Location", "SKU has assigned home location"),
    CONSOLIDATION("CON", "Consolidation", "Prioritize locations with same SKU"),
    VELOCITY_BASED("VEL", "Velocity Based", "Zone by ABC classification"),
    FIFO("FIFO", "FIFO", "First In First Out sequencing"),
    FEFO("FEFO", "FEFO", "First Expiry First Out sequencing"),
    ZONE_SEQUENCE("ZSQ", "Zone Sequence", "Follow zone priority sequence"),
    MULTI_CRITERIA("MUL", "Multi-Criteria", "Weighted scoring of multiple factors"),
    RANDOM("RND", "Random", "Random available location"),
    USER_DIRECTED("USR", "User Directed", "User selects location"),
    CROSSDOCK("XDK", "Crossdock", "Direct to outbound staging");

    private final String code;
    private final String label;
    private final String description;

    AlgorithmType(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }
    public String getDescription() { return description; }

    public static AlgorithmType fromCode(String code) {
        for (AlgorithmType type : values()) {
            if (type.code.equals(code)) return type;
        }
        return DIRECTED;
    }

    public boolean requiresScoring() {
        return this == MULTI_CRITERIA || this == VELOCITY_BASED || this == CONSOLIDATION;
    }

    public boolean isSystemDirected() {
        return this != USER_DIRECTED;
    }
}
