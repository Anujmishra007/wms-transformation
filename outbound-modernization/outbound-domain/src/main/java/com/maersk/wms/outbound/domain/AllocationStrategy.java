package com.maersk.wms.outbound.domain;

/**
 * Allocation strategy enumeration.
 * Determines how inventory is selected for allocation.
 */
public enum AllocationStrategy {

    FIFO("FIFO", "First In First Out", "Allocate oldest inventory first by receipt date"),
    FEFO("FEFO", "First Expiry First Out", "Allocate inventory expiring soonest first"),
    LIFO("LIFO", "Last In First Out", "Allocate newest inventory first"),
    FIFO_BY_LOT("FIFO_LOT", "FIFO by Lot", "FIFO within lot boundaries"),
    FEFO_BY_LOT("FEFO_LOT", "FEFO by Lot", "FEFO within lot boundaries"),
    LOCATION_PRIORITY("LOC_PRI", "Location Priority", "Allocate from priority locations first"),
    PICK_PATH("PATH", "Pick Path Optimized", "Optimize for pick path efficiency"),
    CONSOLIDATE("CONS", "Consolidation", "Minimize number of locations picked from"),
    ZONE_BASED("ZONE", "Zone Based", "Allocate from specific zones"),
    MANUAL("MANUAL", "Manual Selection", "User selects specific inventory");

    private final String code;
    private final String name;
    private final String description;

    AllocationStrategy(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static AllocationStrategy fromCode(String code) {
        for (AllocationStrategy strategy : values()) {
            if (strategy.code.equals(code)) {
                return strategy;
            }
        }
        throw new IllegalArgumentException("Unknown allocation strategy code: " + code);
    }
}
