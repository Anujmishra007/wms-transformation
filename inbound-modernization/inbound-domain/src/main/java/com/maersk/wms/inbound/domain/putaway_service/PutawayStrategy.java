package com.maersk.wms.inbound.domain.putaway_service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Putaway strategy configuration.
 *
 * Defines rules for determining putaway location.
 *
 * Legacy Table: PUTAWAYSTRATEGY
 * Legacy SPs: nsp_GetPutawayStrategy, nsp_DirectedPutaway
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PutawayStrategy {

    private String strategyKey;
    private String strategyName;
    private String description;

    private PutawayStrategyType type;
    private boolean active;
    private int priority;

    // Zone restrictions
    private List<String> allowedZones;
    private List<String> excludedZones;
    private String defaultZone;

    // Location type preferences
    private List<String> preferredLocationTypes;
    private boolean allowMixedSku;
    private boolean allowMixedLot;

    // Capacity rules
    private boolean checkCapacity;
    private boolean consolidate;  // Try to put in location with same SKU

    // FIFO rules
    private boolean enforceFifo;
    private boolean checkExpiry;
    private int minDaysToExpiry;

    // Velocity rules
    private boolean useVelocity;  // A/B/C classification
    private String velocityZoneMapping;  // A=ZONE1, B=ZONE2, etc.

    // Return putaway rules
    private boolean forReturns;
    private String returnZone;
    private List<String> dispositionZoneMapping;

    /**
     * Check if this strategy applies to returns.
     */
    public boolean appliesTo(boolean isReturn, String disposition) {
        if (isReturn && !forReturns) {
            return false;
        }
        return true;
    }

    /**
     * Get zone for a disposition (for returns).
     */
    public String getZoneForDisposition(String disposition) {
        // TODO: Parse dispositionZoneMapping
        return returnZone;
    }
}
