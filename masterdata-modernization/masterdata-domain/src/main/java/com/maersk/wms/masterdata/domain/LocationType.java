package com.maersk.wms.masterdata.domain;

/**
 * Location type enumeration.
 */
public enum LocationType {
    RESERVE("RSV", "Reserve/Bulk Storage"),
    PICKFACE("PKF", "Pick Face"),
    FLOOR("FLR", "Floor Storage"),
    RACK("RCK", "Rack Location"),
    CARTON_FLOW("CFW", "Carton Flow"),
    PALLET_FLOW("PFW", "Pallet Flow"),
    STAGING("STG", "Staging"),
    RECEIVING("RCV", "Receiving"),
    SHIPPING("SHP", "Shipping"),
    QC("QC", "Quality Control"),
    DAMAGE("DMG", "Damage Hold"),
    RETURNS("RTN", "Returns"),
    CROSSDOCK("XDK", "Cross Dock"),
    VIRTUAL("VRT", "Virtual Location"),
    PRODUCTION("PRD", "Production"),
    ASRS("ASR", "Automated Storage");

    private final String code;
    private final String description;

    LocationType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static LocationType fromCode(String code) {
        for (LocationType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return RESERVE;
    }
}
