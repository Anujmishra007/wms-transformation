package com.maersk.wms.outbound.domain.shipping;

/**
 * Service type enumeration matching legacy CODELKUP values.
 */
public enum ServiceType {
    GROUND,
    EXPRESS,
    OVERNIGHT,
    TWO_DAY,
    THREE_DAY,
    FREIGHT_LTL,
    FREIGHT_FTL,
    INTERNATIONAL_ECONOMY,
    INTERNATIONAL_PRIORITY,
    SAME_DAY
}
