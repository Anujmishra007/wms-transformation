package com.maersk.wms.picking.rules;

/**
 * Short pick decision enum.
 */
public enum ShortPickDecision {
    ALLOW,
    DENY,
    REQUIRE_APPROVAL,
    REQUIRE_RECOUNT,
    AUTO_REALLOCATE
}
