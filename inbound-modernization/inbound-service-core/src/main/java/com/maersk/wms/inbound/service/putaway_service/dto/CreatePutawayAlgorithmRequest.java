package com.maersk.wms.inbound.service.putaway_service.dto;

import com.maersk.wms.inbound.domain.putaway_service.AlgorithmType;
import com.maersk.wms.inbound.domain.putaway_service.DispositionZoneRule;
import com.maersk.wms.inbound.domain.putaway_service.ZonePreference;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class CreatePutawayAlgorithmRequest {
    private String algorithmName;
    private String description;
    private AlgorithmType algorithmType;
    private boolean active;
    private int priority;

    // Scoring weights
    private BigDecimal distanceWeight;
    private BigDecimal capacityWeight;
    private BigDecimal consolidationWeight;
    private BigDecimal velocityWeight;
    private BigDecimal proximityWeight;

    // Zone preferences
    private List<ZonePreference> zonePreferences;
    private List<String> allowedZones;
    private List<String> preferredZones;
    private List<String> excludedZones;

    // Location type preferences
    private List<String> preferredLocationTypes;

    // Consolidation rules
    private boolean enableConsolidation;
    private int maxSkusPerLocation;
    private int maxLotsPerLocation;
    private boolean sameSkuOnly;
    private boolean sameLotOnly;

    // Capacity rules
    private boolean checkCapacity;
    private BigDecimal minFillPercent;
    private BigDecimal maxFillPercent;
    private boolean preferEmpty;
    private boolean preferConsolidation;
    private boolean preferNearPick;
    private boolean preferEmptyLocations;
    private boolean preferPartialLocations;

    // Distance optimization
    private boolean optimizeDistance;
    private String distanceMethod;

    // Velocity-based rules
    private boolean useVelocityZoning;
    private String velocityAZone;
    private String velocityBZone;
    private String velocityCZone;

    // FIFO/FEFO rules
    private boolean enforceFifo;
    private boolean enforceFefo;
    private int minDaysToExpiry;

    // Return-specific rules
    private boolean forReturns;
    private String returnDefaultZone;
    private List<DispositionZoneRule> dispositionZoneRules;

    // Fallback behavior
    private String fallbackAlgorithmKey;
    private boolean allowManualOverride;
    private String noLocationFoundAction;

    // Additional parameters
    private Map<String, Object> parameters;
}
