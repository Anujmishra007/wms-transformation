package com.maersk.wms.picking.domain.task_execution_service.model;

/**
 * Task type enumeration.
 */
public enum TaskType {
    STANDARD("STD", "Standard Pick"),
    BATCH("BCH", "Batch Pick"),
    CLUSTER("CLU", "Cluster Pick"),
    ZONE("ZON", "Zone Pick"),
    PUT_TO_LIGHT("PTL", "Put-to-Light"),
    PICK_TO_CART("PTC", "Pick to Cart"),
    REPLENISHMENT("REP", "Replenishment"),
    CYCLE_COUNT("CC", "Cycle Count");

    private final String code;
    private final String description;

    TaskType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static TaskType fromCode(String code) {
        for (TaskType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return STANDARD;
    }
}
