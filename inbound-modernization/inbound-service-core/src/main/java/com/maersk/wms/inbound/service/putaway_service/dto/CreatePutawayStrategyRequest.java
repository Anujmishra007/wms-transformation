package com.maersk.wms.inbound.service.putaway_service.dto;

import com.maersk.wms.inbound.domain.putaway_service.PutawayStrategyType;
import lombok.Data;
import java.util.List;

@Data
public class CreatePutawayStrategyRequest {
    private String strategyName;
    private String description;
    private PutawayStrategyType strategyType;
    private boolean active;
    private int priority;

    // Zone configuration
    private List<String> allowedZones;
    private List<String> preferredZones;
    private List<String> excludedZones;
    private String defaultZone;

    // Location type preferences
    private List<String> preferredLocationTypes;

    // Mixing rules
    private boolean allowMixedSku;
    private boolean allowMixedLot;
    private boolean allowMixedStorer;
    private int maxSkusPerLocation;
    private int maxLotsPerLocation;

    // Capacity rules
    private boolean checkCapacity;
    private boolean consolidate;
    private boolean allowCrossdock;

    // FIFO rules
    private boolean enforceFifo;
    private boolean checkExpiry;
    private int minDaysToExpiry;

    // Velocity rules
    private boolean useVelocity;
    private String velocityZoneMapping;
    private boolean enforceVelocityClass;
    private boolean enforceAbcClass;

    // Return putaway rules
    private boolean forReturns;
    private String returnZone;
    private List<String> dispositionZoneMapping;

    // Rotation rules
    private boolean enforceRotation;
    private String rotationType;
}
