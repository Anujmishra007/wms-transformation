package com.maersk.wms.outbound.domain.allocation_service.model;

/**
 * PickHeader type enumeration.
 */
public enum PickHeaderType {
    STANDARD("STD", "Standard Pick"),
    BATCH("BCH", "Batch Pick"),
    CLUSTER("CLU", "Cluster Pick"),
    ZONE("ZON", "Zone Pick"),
    WAVE("WAV", "Wave Pick"),
    REPLENISHMENT("REP", "Replenishment Pick");

    private final String code;
    private final String description;

    PickHeaderType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static PickHeaderType fromCode(String code) {
        for (PickHeaderType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return STANDARD;
    }
}
