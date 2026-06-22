package com.maersk.wms.masterdata.domain;

/**
 * Customer type enumeration.
 */
public enum CustomerType {
    OWNER("OWN", "Owner/Client"),
    SHIPTO("SHP", "Ship-To Customer"),
    BILLTO("BIL", "Bill-To Customer"),
    SUPPLIER("SUP", "Supplier/Vendor"),
    CARRIER("CAR", "Carrier"),
    CONSIGNEE("CON", "Consignee"),
    BROKER("BRK", "Broker"),
    THIRDPARTY("3PL", "Third Party");

    private final String code;
    private final String description;

    CustomerType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static CustomerType fromCode(String code) {
        for (CustomerType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return SHIPTO;
    }
}
