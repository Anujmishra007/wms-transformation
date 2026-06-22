package com.maersk.wms.inbound.domain.operations_service;

/**
 * Types of returns in Operations subdomain.
 */
public enum ReturnType {
    CUSTOMER("C", "Customer Return"),
    EXCHANGE("E", "Exchange"),
    DAMAGED("D", "Damaged in Transit"),
    DEFECTIVE("F", "Defective"),
    WRONG_ITEM("W", "Wrong Item"),
    RECALL("R", "Recall"),
    REFUSED("RF", "Refused Delivery"),
    ECOMMERCE("EC", "E-Commerce Return");

    private final String code;
    private final String label;

    ReturnType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }

    public static ReturnType fromCode(String code) {
        for (ReturnType type : values()) {
            if (type.code.equals(code)) return type;
        }
        return CUSTOMER;
    }

    public boolean requiresInspection() { return this != EXCHANGE; }
}
