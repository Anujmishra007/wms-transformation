package com.maersk.wms.inbound.domain.putaway_service;

/**
 * Types of Location Allocation in putaway-service subdomain.
 */
public enum AllocationType {
    PUTAWAY("PA", "Putaway Allocation"),
    REPLENISHMENT("RP", "Replenishment Allocation"),
    PICK("PK", "Pick Allocation"),
    STAGING("ST", "Staging Allocation"),
    CROSSDOCK("XD", "Crossdock Allocation"),
    RETURN("RT", "Return Allocation"),
    RESERVE("RS", "Reserve Allocation"),
    QUALITY_HOLD("QH", "Quality Hold Allocation");

    private final String code;
    private final String label;

    AllocationType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }

    public static AllocationType fromCode(String code) {
        for (AllocationType type : values()) {
            if (type.code.equals(code)) return type;
        }
        return PUTAWAY;
    }

    public boolean isPutawayRelated() {
        return this == PUTAWAY || this == REPLENISHMENT || this == RETURN;
    }
}
