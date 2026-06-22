package com.maersk.wms.outbound.domain.shipping;

/**
 * Master Bill of Lading status enumeration matching legacy system.
 * Status pattern: 0=New → 1-8=In Progress → 9=Closed
 */
public enum MbolStatus {
    NEW("0"),
    PACKED("1"),
    MANIFESTED("5"),
    LOADED("6"),
    IN_TRANSIT("7"),
    SHIPPED("9"),
    DELIVERED("D"),
    CANCELLED("CANC");

    private final String legacyCode;

    MbolStatus(String legacyCode) {
        this.legacyCode = legacyCode;
    }

    public String getLegacyCode() {
        return legacyCode;
    }

    public static MbolStatus fromLegacyCode(String code) {
        for (MbolStatus status : values()) {
            if (status.legacyCode.equals(code)) {
                return status;
            }
        }
        return NEW;
    }
}
