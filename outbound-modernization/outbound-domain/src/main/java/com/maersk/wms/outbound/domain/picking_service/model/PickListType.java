package com.maersk.wms.outbound.domain.picking_service.model;

/**
 * PickList type enumeration.
 */
public enum PickListType {
    STANDARD("STD", "Standard Pick List"),
    BATCH("BCH", "Batch Pick List"),
    CLUSTER("CLU", "Cluster Pick List"),
    ZONE("ZON", "Zone Pick List"),
    PRIORITY("PRI", "Priority Pick List"),
    REPLENISHMENT("REP", "Replenishment Pick List");

    private final String code;
    private final String description;

    PickListType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static PickListType fromCode(String code) {
        for (PickListType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return STANDARD;
    }
}
