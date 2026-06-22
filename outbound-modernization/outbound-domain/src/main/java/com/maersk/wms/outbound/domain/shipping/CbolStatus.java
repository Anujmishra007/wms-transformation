package com.maersk.wms.outbound.domain.shipping;

/**
 * Commercial Bill of Lading status enumeration.
 */
public enum CbolStatus {
    NEW("0"),
    PACKED("1"),
    LABELED("2"),
    MANIFESTED("5"),
    SHIPPED("9"),
    DELIVERED("D"),
    CANCELLED("CANC");

    private final String legacyCode;

    CbolStatus(String legacyCode) {
        this.legacyCode = legacyCode;
    }

    public String getLegacyCode() {
        return legacyCode;
    }

    public static CbolStatus fromLegacyCode(String code) {
        for (CbolStatus status : values()) {
            if (status.legacyCode.equals(code)) {
                return status;
            }
        }
        return NEW;
    }
}
