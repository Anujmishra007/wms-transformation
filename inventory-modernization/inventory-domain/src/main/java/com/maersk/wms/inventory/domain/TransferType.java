package com.maersk.wms.inventory.domain;

/**
 * Transfer type enumeration.
 */
public enum TransferType {
    /** Replenishment from reserve to active */
    REPLENISHMENT("RPL", "Replenishment"),

    /** Manual move */
    MANUAL_MOVE("MOV", "Manual Move"),

    /** Consolidation */
    CONSOLIDATION("CON", "Consolidation"),

    /** Relocation */
    RELOCATION("REL", "Relocation"),

    /** Cross-dock */
    CROSS_DOCK("XDK", "Cross Dock"),

    /** Putaway */
    PUTAWAY("PUT", "Putaway"),

    /** Return to stock */
    RETURN_TO_STOCK("RTS", "Return to Stock");

    private final String code;
    private final String description;

    TransferType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static TransferType fromCode(String code) {
        for (TransferType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown transfer type code: " + code);
    }
}
