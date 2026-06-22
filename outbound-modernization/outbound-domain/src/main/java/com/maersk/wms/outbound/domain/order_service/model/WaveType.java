package com.maersk.wms.outbound.domain.order_service.model;

/**
 * Wave type enumeration.
 */
public enum WaveType {
    STANDARD("STD", "Standard Wave"),
    PRIORITY("PRI", "Priority Wave"),
    REPLENISHMENT("REP", "Replenishment Wave"),
    BULK("BLK", "Bulk Wave"),
    SMALL_PARCEL("SMP", "Small Parcel Wave"),
    TRANSFER("TRF", "Transfer Wave");

    private final String code;
    private final String description;

    WaveType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static WaveType fromCode(String code) {
        for (WaveType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return STANDARD;
    }
}
