package com.maersk.wms.outbound.shared.kernel.events;

/**
 * Enumeration of bounded contexts in the Outbound domain.
 * Used for event routing and service identification.
 */
public enum OutboundBoundedContext {
    ORDER("order-service", "Order Management"),
    ALLOCATION("inventory-allocation-service", "Inventory Allocation"),
    PICKING("picking-operations-service", "Picking Operations"),
    SHIPPING("shipping-service", "Shipping Operations");

    private final String serviceId;
    private final String description;

    OutboundBoundedContext(String serviceId, String description) {
        this.serviceId = serviceId;
        this.description = description;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getDescription() {
        return description;
    }
}
