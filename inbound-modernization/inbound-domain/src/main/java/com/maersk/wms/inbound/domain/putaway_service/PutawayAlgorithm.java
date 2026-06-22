package com.maersk.wms.inbound.domain.putaway_service;

import com.maersk.wms.inbound.shared.kernel.identifiers.LocationKey;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Putaway Algorithm entity for putaway-service subdomain.
 * Defines the algorithm/rules for determining optimal putaway locations.
 * Supports multiple algorithm types with configurable parameters.
 *
 * Legacy SPs: nsp_DirectedPutaway, nsp_GetPutawayLocation, nsp_PutawayOptimize
 */
public class PutawayAlgorithm {

    private String algorithmKey;
    private String algorithmName;
    private String description;
    private AlgorithmType algorithmType;
    private boolean active;
    private int priority;

    // Scoring weights for multi-criteria algorithm
    private BigDecimal distanceWeight;
    private BigDecimal capacityWeight;
    private BigDecimal consolidationWeight;
    private BigDecimal velocityWeight;
    private BigDecimal fifoWeight;

    // Zone preferences
    private List<ZonePreference> zonePreferences = new ArrayList<>();
    private boolean respectZoneSequence;

    // Location type preferences
    private List<String> preferredLocationTypes = new ArrayList<>();
    private boolean respectLocationTypeSequence;

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
    private boolean preferEmptyLocations;
    private boolean preferPartialLocations;

    // Distance optimization
    private boolean optimizeDistance;
    private String distanceMethod; // MANHATTAN, EUCLIDEAN, SEQUENCE
    private LocationKey referencePoint;

    // Velocity-based rules (ABC classification)
    private boolean useVelocityZoning;
    private String velocityAZone;
    private String velocityBZone;
    private String velocityCZone;

    // FIFO/FEFO rules
    private boolean enforceFifo;
    private boolean enforceFefo;
    private int maxDaysToExpiry;
    private int minDaysToExpiry;

    // Return-specific rules
    private boolean forReturns;
    private String returnDefaultZone;
    private List<DispositionZoneRule> dispositionZoneRules = new ArrayList<>();

    // Fallback behavior
    private String fallbackAlgorithmKey;
    private boolean allowManualOverride;
    private String noLocationFoundAction; // QUEUE, STAGE, ALERT, REJECT

    public PutawayAlgorithm() {}

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    // Domain methods

    public BigDecimal calculateScore(LocationAllocation location, PutawayContext context) {
        BigDecimal score = BigDecimal.ZERO;

        // Distance score
        if (optimizeDistance && distanceWeight != null) {
            BigDecimal distScore = calculateDistanceScore(location, context);
            score = score.add(distScore.multiply(distanceWeight));
        }

        // Capacity score
        if (checkCapacity && capacityWeight != null) {
            BigDecimal capScore = calculateCapacityScore(location);
            score = score.add(capScore.multiply(capacityWeight));
        }

        // Consolidation score
        if (enableConsolidation && consolidationWeight != null) {
            BigDecimal consScore = calculateConsolidationScore(location, context);
            score = score.add(consScore.multiply(consolidationWeight));
        }

        // Velocity score
        if (useVelocityZoning && velocityWeight != null) {
            BigDecimal velScore = calculateVelocityScore(location, context);
            score = score.add(velScore.multiply(velocityWeight));
        }

        return score;
    }

    private BigDecimal calculateDistanceScore(LocationAllocation location, PutawayContext context) {
        // Closer locations get higher scores
        // Implementation would calculate actual distance
        return BigDecimal.valueOf(100 - location.getSequence());
    }

    private BigDecimal calculateCapacityScore(LocationAllocation location) {
        BigDecimal utilization = location.getUtilizationPercent();
        if (preferEmptyLocations) {
            return BigDecimal.valueOf(100).subtract(utilization);
        } else if (preferPartialLocations) {
            // Prefer 50-80% utilized locations
            if (utilization.compareTo(BigDecimal.valueOf(50)) >= 0
                && utilization.compareTo(BigDecimal.valueOf(80)) <= 0) {
                return BigDecimal.valueOf(100);
            }
            return BigDecimal.valueOf(50);
        }
        return BigDecimal.valueOf(100).subtract(utilization);
    }

    private BigDecimal calculateConsolidationScore(LocationAllocation location, PutawayContext context) {
        // Same SKU gets high score
        if (location.getSkuKey() != null && location.getSkuKey().equals(context.getSkuKey())) {
            return BigDecimal.valueOf(100);
        }
        if (location.getCurrentSkuCount() == 0) {
            return BigDecimal.valueOf(50);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateVelocityScore(LocationAllocation location, PutawayContext context) {
        String velocityClass = context.getVelocityClass();
        String locationZone = location.getZone();

        if ("A".equals(velocityClass) && locationZone.equals(velocityAZone)) {
            return BigDecimal.valueOf(100);
        }
        if ("B".equals(velocityClass) && locationZone.equals(velocityBZone)) {
            return BigDecimal.valueOf(100);
        }
        if ("C".equals(velocityClass) && locationZone.equals(velocityCZone)) {
            return BigDecimal.valueOf(100);
        }
        return BigDecimal.ZERO;
    }

    public boolean meetsConstraints(LocationAllocation location, PutawayContext context) {
        // Check capacity
        if (checkCapacity && !location.hasCapacityFor(context.getQuantity())) {
            return false;
        }

        // Check SKU mixing
        if (sameSkuOnly && !location.canAcceptSku(context.getSkuKey())) {
            return false;
        }

        // Check max SKUs
        if (maxSkusPerLocation > 0 && location.getCurrentSkuCount() >= maxSkusPerLocation) {
            if (!location.getSkuKey().equals(context.getSkuKey())) {
                return false;
            }
        }

        // Check lot mixing
        if (sameLotOnly && !location.canAcceptLot(context.getLot())) {
            return false;
        }

        // Check fill percentage
        if (maxFillPercent != null) {
            BigDecimal util = location.getUtilizationPercent();
            if (util.compareTo(maxFillPercent) > 0) {
                return false;
            }
        }

        return true;
    }

    public String getZoneForDisposition(String disposition) {
        for (DispositionZoneRule rule : dispositionZoneRules) {
            if (rule.getDisposition().equals(disposition)) {
                return rule.getZone();
            }
        }
        return returnDefaultZone;
    }

    // Getters and Setters
    public String getAlgorithmKey() { return algorithmKey; }
    public void setAlgorithmKey(String algorithmKey) { this.algorithmKey = algorithmKey; }

    public String getAlgorithmName() { return algorithmName; }
    public void setAlgorithmName(String algorithmName) { this.algorithmName = algorithmName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public AlgorithmType getAlgorithmType() { return algorithmType; }
    public void setAlgorithmType(AlgorithmType algorithmType) { this.algorithmType = algorithmType; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public BigDecimal getDistanceWeight() { return distanceWeight; }
    public void setDistanceWeight(BigDecimal distanceWeight) { this.distanceWeight = distanceWeight; }

    public BigDecimal getCapacityWeight() { return capacityWeight; }
    public void setCapacityWeight(BigDecimal capacityWeight) { this.capacityWeight = capacityWeight; }

    public BigDecimal getConsolidationWeight() { return consolidationWeight; }
    public void setConsolidationWeight(BigDecimal consolidationWeight) { this.consolidationWeight = consolidationWeight; }

    public BigDecimal getVelocityWeight() { return velocityWeight; }
    public void setVelocityWeight(BigDecimal velocityWeight) { this.velocityWeight = velocityWeight; }

    public BigDecimal getFifoWeight() { return fifoWeight; }
    public void setFifoWeight(BigDecimal fifoWeight) { this.fifoWeight = fifoWeight; }

    public List<ZonePreference> getZonePreferences() { return zonePreferences; }
    public void setZonePreferences(List<ZonePreference> zonePreferences) { this.zonePreferences = zonePreferences; }

    public boolean isRespectZoneSequence() { return respectZoneSequence; }
    public void setRespectZoneSequence(boolean respectZoneSequence) { this.respectZoneSequence = respectZoneSequence; }

    public List<String> getPreferredLocationTypes() { return preferredLocationTypes; }
    public void setPreferredLocationTypes(List<String> preferredLocationTypes) { this.preferredLocationTypes = preferredLocationTypes; }

    public boolean isRespectLocationTypeSequence() { return respectLocationTypeSequence; }
    public void setRespectLocationTypeSequence(boolean respectLocationTypeSequence) { this.respectLocationTypeSequence = respectLocationTypeSequence; }

    public boolean isEnableConsolidation() { return enableConsolidation; }
    public void setEnableConsolidation(boolean enableConsolidation) { this.enableConsolidation = enableConsolidation; }

    public int getMaxSkusPerLocation() { return maxSkusPerLocation; }
    public void setMaxSkusPerLocation(int maxSkusPerLocation) { this.maxSkusPerLocation = maxSkusPerLocation; }

    public int getMaxLotsPerLocation() { return maxLotsPerLocation; }
    public void setMaxLotsPerLocation(int maxLotsPerLocation) { this.maxLotsPerLocation = maxLotsPerLocation; }

    public boolean isSameSkuOnly() { return sameSkuOnly; }
    public void setSameSkuOnly(boolean sameSkuOnly) { this.sameSkuOnly = sameSkuOnly; }

    public boolean isSameLotOnly() { return sameLotOnly; }
    public void setSameLotOnly(boolean sameLotOnly) { this.sameLotOnly = sameLotOnly; }

    public boolean isCheckCapacity() { return checkCapacity; }
    public void setCheckCapacity(boolean checkCapacity) { this.checkCapacity = checkCapacity; }

    public BigDecimal getMinFillPercent() { return minFillPercent; }
    public void setMinFillPercent(BigDecimal minFillPercent) { this.minFillPercent = minFillPercent; }

    public BigDecimal getMaxFillPercent() { return maxFillPercent; }
    public void setMaxFillPercent(BigDecimal maxFillPercent) { this.maxFillPercent = maxFillPercent; }

    public boolean isPreferEmptyLocations() { return preferEmptyLocations; }
    public void setPreferEmptyLocations(boolean preferEmptyLocations) { this.preferEmptyLocations = preferEmptyLocations; }

    public boolean isPreferPartialLocations() { return preferPartialLocations; }
    public void setPreferPartialLocations(boolean preferPartialLocations) { this.preferPartialLocations = preferPartialLocations; }

    public boolean isOptimizeDistance() { return optimizeDistance; }
    public void setOptimizeDistance(boolean optimizeDistance) { this.optimizeDistance = optimizeDistance; }

    public String getDistanceMethod() { return distanceMethod; }
    public void setDistanceMethod(String distanceMethod) { this.distanceMethod = distanceMethod; }

    public LocationKey getReferencePoint() { return referencePoint; }
    public void setReferencePoint(LocationKey referencePoint) { this.referencePoint = referencePoint; }

    public boolean isUseVelocityZoning() { return useVelocityZoning; }
    public void setUseVelocityZoning(boolean useVelocityZoning) { this.useVelocityZoning = useVelocityZoning; }

    public String getVelocityAZone() { return velocityAZone; }
    public void setVelocityAZone(String velocityAZone) { this.velocityAZone = velocityAZone; }

    public String getVelocityBZone() { return velocityBZone; }
    public void setVelocityBZone(String velocityBZone) { this.velocityBZone = velocityBZone; }

    public String getVelocityCZone() { return velocityCZone; }
    public void setVelocityCZone(String velocityCZone) { this.velocityCZone = velocityCZone; }

    public boolean isEnforceFifo() { return enforceFifo; }
    public void setEnforceFifo(boolean enforceFifo) { this.enforceFifo = enforceFifo; }

    public boolean isEnforceFefo() { return enforceFefo; }
    public void setEnforceFefo(boolean enforceFefo) { this.enforceFefo = enforceFefo; }

    public int getMaxDaysToExpiry() { return maxDaysToExpiry; }
    public void setMaxDaysToExpiry(int maxDaysToExpiry) { this.maxDaysToExpiry = maxDaysToExpiry; }

    public int getMinDaysToExpiry() { return minDaysToExpiry; }
    public void setMinDaysToExpiry(int minDaysToExpiry) { this.minDaysToExpiry = minDaysToExpiry; }

    public boolean isForReturns() { return forReturns; }
    public void setForReturns(boolean forReturns) { this.forReturns = forReturns; }

    public String getReturnDefaultZone() { return returnDefaultZone; }
    public void setReturnDefaultZone(String returnDefaultZone) { this.returnDefaultZone = returnDefaultZone; }

    public List<DispositionZoneRule> getDispositionZoneRules() { return dispositionZoneRules; }
    public void setDispositionZoneRules(List<DispositionZoneRule> dispositionZoneRules) { this.dispositionZoneRules = dispositionZoneRules; }

    public String getFallbackAlgorithmKey() { return fallbackAlgorithmKey; }
    public void setFallbackAlgorithmKey(String fallbackAlgorithmKey) { this.fallbackAlgorithmKey = fallbackAlgorithmKey; }

    public boolean isAllowManualOverride() { return allowManualOverride; }
    public void setAllowManualOverride(boolean allowManualOverride) { this.allowManualOverride = allowManualOverride; }

    public String getNoLocationFoundAction() { return noLocationFoundAction; }
    public void setNoLocationFoundAction(String noLocationFoundAction) { this.noLocationFoundAction = noLocationFoundAction; }

    public static class Builder {
        private final PutawayAlgorithm algorithm = new PutawayAlgorithm();

        public Builder algorithmKey(String key) { algorithm.algorithmKey = key; return this; }
        public Builder algorithmName(String name) { algorithm.algorithmName = name; return this; }
        public Builder algorithmType(AlgorithmType type) { algorithm.algorithmType = type; return this; }
        public Builder active(boolean val) { algorithm.active = val; return this; }
        public Builder distanceWeight(BigDecimal w) { algorithm.distanceWeight = w; return this; }
        public Builder capacityWeight(BigDecimal w) { algorithm.capacityWeight = w; return this; }
        public Builder consolidationWeight(BigDecimal w) { algorithm.consolidationWeight = w; return this; }
        public Builder velocityWeight(BigDecimal w) { algorithm.velocityWeight = w; return this; }
        public Builder checkCapacity(boolean val) { algorithm.checkCapacity = val; return this; }
        public Builder enableConsolidation(boolean val) { algorithm.enableConsolidation = val; return this; }
        public Builder useVelocityZoning(boolean val) { algorithm.useVelocityZoning = val; return this; }

        public PutawayAlgorithm build() {
            return algorithm;
        }
    }
}
