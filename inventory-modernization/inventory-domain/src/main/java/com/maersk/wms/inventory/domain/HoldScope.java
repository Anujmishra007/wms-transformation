package com.maersk.wms.inventory.domain;

/**
 * Hold scope enumeration.
 */
public enum HoldScope {
    /** Hold specific LPN */
    LPN,

    /** Hold specific lot */
    LOT,

    /** Hold entire SKU */
    SKU,

    /** Hold specific location */
    LOCATION,

    /** Hold lot + SKU combination */
    LOT_SKU,

    /** Hold SKU + location combination */
    SKU_LOCATION
}
