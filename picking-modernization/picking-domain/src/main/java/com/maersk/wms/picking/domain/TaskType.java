package com.maersk.wms.picking.domain;

/**
 * Task type enumeration for different picking operations.
 */
public enum TaskType {
    /** Standard piece picking - FN839 */
    PIECE_PICK("PP", "Piece Pick"),

    /** Case picking */
    CASE_PICK("CP", "Case Pick"),

    /** Pallet picking */
    PALLET_PICK("PL", "Pallet Pick"),

    /** Cluster picking - multiple orders */
    CLUSTER_PICK("CL", "Cluster Pick"),

    /** Batch picking */
    BATCH_PICK("BP", "Batch Pick"),

    /** Zone picking */
    ZONE_PICK("ZP", "Zone Pick"),

    /** Wave picking */
    WAVE_PICK("WP", "Wave Pick"),

    /** Replenishment task */
    REPLENISHMENT("RP", "Replenishment"),

    /** Cycle count */
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
        throw new IllegalArgumentException("Unknown task type code: " + code);
    }
}
