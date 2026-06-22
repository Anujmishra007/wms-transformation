package com.maersk.wms.outbound.domain;

/**
 * Order priority enumeration.
 */
public enum OrderPriority {

    CRITICAL(1, "Critical - Same Day"),
    HIGH(2, "High - Rush"),
    NORMAL(5, "Normal"),
    LOW(9, "Low - Economy");

    private final int level;
    private final String description;

    OrderPriority(int level, String description) {
        this.level = level;
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }

    public static OrderPriority fromLevel(int level) {
        for (OrderPriority priority : values()) {
            if (priority.level == level) {
                return priority;
            }
        }
        return NORMAL;
    }
}
