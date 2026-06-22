package com.maersk.wms.inbound.domain.operations_service;

/**
 * Types of Putaway Tasks in Operations subdomain.
 */
public enum PutawayTaskType {
    STANDARD("STD", "Standard Putaway", false),
    DIRECT("DIR", "Direct Putaway", false),
    CROSSDOCK("XDK", "Crossdock Putaway", true),
    RETURN("RET", "Return Putaway", false),
    TRANSFER("TRF", "Transfer Putaway", false),
    REPLENISHMENT("RPL", "Replenishment", false),
    QUALITY_HOLD("QH", "Quality Hold", false);

    private final String code;
    private final String label;
    private final boolean skipStorage;

    PutawayTaskType(String code, String label, boolean skipStorage) {
        this.code = code;
        this.label = label;
        this.skipStorage = skipStorage;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }
    public boolean isSkipStorage() { return skipStorage; }

    public static PutawayTaskType fromCode(String code) {
        for (PutawayTaskType type : values()) {
            if (type.code.equals(code)) return type;
        }
        return STANDARD;
    }

    public boolean requiresLocationAllocation() {
        return this != CROSSDOCK && this != QUALITY_HOLD;
    }
}
