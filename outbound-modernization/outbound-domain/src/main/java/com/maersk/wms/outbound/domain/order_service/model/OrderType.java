package com.maersk.wms.outbound.domain.order_service.model;

/**
 * Order type enumeration.
 */
public enum OrderType {
    STANDARD("STD", "Standard Order"),
    RUSH("RSH", "Rush Order"),
    BACKORDER("BCK", "Back Order"),
    TRANSFER("TRF", "Transfer Order"),
    RETURN_TO_VENDOR("RTV", "Return to Vendor"),
    SAMPLE("SMP", "Sample Order"),
    WILL_CALL("WC", "Will Call");

    private final String code;
    private final String description;

    OrderType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static OrderType fromCode(String code) {
        for (OrderType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return STANDARD;
    }
}
