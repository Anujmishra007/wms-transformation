package com.maersk.wms.outbound.domain.order_service.model;

/**
 * Order priority enumeration.
 */
public enum OrderPriority {
    CRITICAL(1, "Critical - Same Day"),
    HIGH(2, "High Priority"),
    NORMAL(5, "Normal Priority"),
    LOW(8, "Low Priority"),
    HOLD(9, "On Hold");

    private final int priority;
    private final String description;

    OrderPriority(int priority, String description) {
        this.priority = priority;
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public String getDescription() {
        return description;
    }

    public static OrderPriority fromPriority(int priority) {
        for (OrderPriority p : values()) {
            if (p.priority == priority) {
                return p;
            }
        }
        return NORMAL;
    }

    public boolean isHigherThan(OrderPriority other) {
        return this.priority < other.priority;
    }
}
