package com.maersk.wms.inbound.domain.putaway_service;

import com.maersk.wms.inbound.shared.kernel.identifiers.SkuKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Crossdock Strategy entity for putaway-service subdomain.
 * Defines rules and algorithms for crossdock operations.
 * Determines when and how to route inventory directly to outbound.
 *
 * Legacy SPs: nsp_CrossDockCheck, nsp_GetCrossDockDemand, nsp_AllocateCrossDock
 */
public class CrossdockStrategy {

    private String strategyKey;
    private String strategyName;
    private String description;
    private CrossdockStrategyType strategyType;
    private boolean active;
    private int priority;

    // Matching criteria
    private boolean matchBySku;
    private boolean matchByLot;
    private boolean matchByStorer;
    private boolean matchByOrder;
    private boolean matchByWave;
    private boolean matchByShipment;

    // Demand timing
    private int demandHorizonDays;
    private int demandHorizonHours;
    private boolean checkFutureDemand;
    private boolean checkCurrentDemand;

    // Quantity rules
    private boolean allowPartialMatch;
    private BigDecimal minMatchPercent;
    private BigDecimal minQuantity;
    private BigDecimal maxQuantity;

    // Priority rules
    private boolean prioritizeOldestDemand;
    private boolean prioritizeHighestPriority;
    private boolean prioritizeNearestShipDate;
    private boolean prioritizeFullPallets;

    // Zone and location rules
    private List<String> crossdockZones = new ArrayList<>();
    private List<String> stagingZones = new ArrayList<>();
    private String defaultStagingZone;
    private boolean autoAssignStaging;

    // SKU restrictions
    private List<String> allowedSkuTypes = new ArrayList<>();
    private List<String> excludedSkuTypes = new ArrayList<>();
    private boolean excludeHazmat;
    private boolean excludeTemperatureControlled;

    // Storer restrictions
    private List<StorerKey> allowedStorers = new ArrayList<>();
    private List<StorerKey> excludedStorers = new ArrayList<>();
    private boolean storerOptIn; // Only storers with crossdock flag

    // Order type restrictions
    private List<String> allowedOrderTypes = new ArrayList<>();
    private List<String> excludedOrderTypes = new ArrayList<>();

    // Carrier/Route restrictions
    private List<String> allowedCarriers = new ArrayList<>();
    private List<String> allowedRoutes = new ArrayList<>();

    // Time windows
    private boolean checkTimeWindow;
    private int minLeadTimeHours;
    private int maxLeadTimeHours;

    // Fallback behavior
    private boolean fallbackToStorage;
    private String fallbackStrategyKey;
    private String noMatchAction; // STORAGE, STAGE, ALERT

    public CrossdockStrategy() {}

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    // Domain methods

    public boolean isEligible(CrossdockCandidate candidate) {
        // Check SKU type restrictions
        if (!allowedSkuTypes.isEmpty() && !allowedSkuTypes.contains(candidate.getSkuType())) {
            return false;
        }
        if (excludedSkuTypes.contains(candidate.getSkuType())) {
            return false;
        }

        // Check hazmat/temperature restrictions
        if (excludeHazmat && candidate.isHazmat()) {
            return false;
        }
        if (excludeTemperatureControlled && candidate.isTemperatureControlled()) {
            return false;
        }

        // Check storer restrictions
        if (!allowedStorers.isEmpty() && !allowedStorers.contains(candidate.getStorerKey())) {
            return false;
        }
        if (excludedStorers.contains(candidate.getStorerKey())) {
            return false;
        }

        // Check order type restrictions
        if (!allowedOrderTypes.isEmpty() && !allowedOrderTypes.contains(candidate.getOrderType())) {
            return false;
        }
        if (excludedOrderTypes.contains(candidate.getOrderType())) {
            return false;
        }

        // Check carrier restrictions
        if (!allowedCarriers.isEmpty() && !allowedCarriers.contains(candidate.getCarrierCode())) {
            return false;
        }

        return true;
    }

    public boolean matchesDemand(CrossdockCandidate candidate, CrossdockDemand demand) {
        // SKU match is required
        if (matchBySku && !candidate.getSkuKey().equals(demand.getSkuKey())) {
            return false;
        }

        // Lot match if required
        if (matchByLot && candidate.getLot() != null && !candidate.getLot().equals(demand.getLot())) {
            return false;
        }

        // Storer match if required
        if (matchByStorer && !candidate.getStorerKey().equals(demand.getStorerKey())) {
            return false;
        }

        // Order match if required (planned crossdock)
        if (matchByOrder && candidate.getOrderKey() != null && !candidate.getOrderKey().equals(demand.getOrderKey())) {
            return false;
        }

        // Wave match if required
        if (matchByWave && candidate.getWaveKey() != null && !candidate.getWaveKey().equals(demand.getWaveKey())) {
            return false;
        }

        // Quantity check
        if (minQuantity != null && candidate.getQuantity().compareTo(minQuantity) < 0) {
            return false;
        }

        return true;
    }

    public String getStagingZone(CrossdockCandidate candidate) {
        // Could have logic to pick staging zone based on route/carrier
        if (!stagingZones.isEmpty()) {
            return stagingZones.get(0);
        }
        return defaultStagingZone;
    }

    public int calculatePriority(CrossdockDemand demand) {
        int score = 0;

        if (prioritizeOldestDemand && demand.getCreatedAt() != null) {
            // Older demands get higher priority
            long daysOld = java.time.temporal.ChronoUnit.DAYS.between(
                demand.getCreatedAt(), java.time.Instant.now());
            score += (int) daysOld;
        }

        if (prioritizeHighestPriority) {
            score += demand.getPriority() * 10;
        }

        if (prioritizeNearestShipDate && demand.getShipDate() != null) {
            long daysUntilShip = java.time.temporal.ChronoUnit.DAYS.between(
                java.time.LocalDate.now(), demand.getShipDate());
            score += (int) (100 - daysUntilShip);
        }

        return score;
    }

    public boolean shouldCrossdock(CrossdockCandidate candidate, List<CrossdockDemand> availableDemand) {
        if (!active) return false;
        if (!isEligible(candidate)) return false;
        if (availableDemand.isEmpty()) return false;

        // Check if any demand matches
        for (CrossdockDemand demand : availableDemand) {
            if (matchesDemand(candidate, demand)) {
                return true;
            }
        }

        return false;
    }

    // Getters and Setters
    public String getStrategyKey() { return strategyKey; }
    public void setStrategyKey(String strategyKey) { this.strategyKey = strategyKey; }

    public String getStrategyName() { return strategyName; }
    public void setStrategyName(String strategyName) { this.strategyName = strategyName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public CrossdockStrategyType getStrategyType() { return strategyType; }
    public void setStrategyType(CrossdockStrategyType strategyType) { this.strategyType = strategyType; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public boolean isMatchBySku() { return matchBySku; }
    public void setMatchBySku(boolean matchBySku) { this.matchBySku = matchBySku; }

    public boolean isMatchByLot() { return matchByLot; }
    public void setMatchByLot(boolean matchByLot) { this.matchByLot = matchByLot; }

    public boolean isMatchByStorer() { return matchByStorer; }
    public void setMatchByStorer(boolean matchByStorer) { this.matchByStorer = matchByStorer; }

    public boolean isMatchByOrder() { return matchByOrder; }
    public void setMatchByOrder(boolean matchByOrder) { this.matchByOrder = matchByOrder; }

    public boolean isMatchByWave() { return matchByWave; }
    public void setMatchByWave(boolean matchByWave) { this.matchByWave = matchByWave; }

    public boolean isMatchByShipment() { return matchByShipment; }
    public void setMatchByShipment(boolean matchByShipment) { this.matchByShipment = matchByShipment; }

    public int getDemandHorizonDays() { return demandHorizonDays; }
    public void setDemandHorizonDays(int demandHorizonDays) { this.demandHorizonDays = demandHorizonDays; }

    public int getDemandHorizonHours() { return demandHorizonHours; }
    public void setDemandHorizonHours(int demandHorizonHours) { this.demandHorizonHours = demandHorizonHours; }

    public boolean isCheckFutureDemand() { return checkFutureDemand; }
    public void setCheckFutureDemand(boolean checkFutureDemand) { this.checkFutureDemand = checkFutureDemand; }

    public boolean isCheckCurrentDemand() { return checkCurrentDemand; }
    public void setCheckCurrentDemand(boolean checkCurrentDemand) { this.checkCurrentDemand = checkCurrentDemand; }

    public boolean isAllowPartialMatch() { return allowPartialMatch; }
    public void setAllowPartialMatch(boolean allowPartialMatch) { this.allowPartialMatch = allowPartialMatch; }

    public BigDecimal getMinMatchPercent() { return minMatchPercent; }
    public void setMinMatchPercent(BigDecimal minMatchPercent) { this.minMatchPercent = minMatchPercent; }

    public BigDecimal getMinQuantity() { return minQuantity; }
    public void setMinQuantity(BigDecimal minQuantity) { this.minQuantity = minQuantity; }

    public BigDecimal getMaxQuantity() { return maxQuantity; }
    public void setMaxQuantity(BigDecimal maxQuantity) { this.maxQuantity = maxQuantity; }

    public boolean isPrioritizeOldestDemand() { return prioritizeOldestDemand; }
    public void setPrioritizeOldestDemand(boolean prioritizeOldestDemand) { this.prioritizeOldestDemand = prioritizeOldestDemand; }

    public boolean isPrioritizeHighestPriority() { return prioritizeHighestPriority; }
    public void setPrioritizeHighestPriority(boolean prioritizeHighestPriority) { this.prioritizeHighestPriority = prioritizeHighestPriority; }

    public boolean isPrioritizeNearestShipDate() { return prioritizeNearestShipDate; }
    public void setPrioritizeNearestShipDate(boolean prioritizeNearestShipDate) { this.prioritizeNearestShipDate = prioritizeNearestShipDate; }

    public boolean isPrioritizeFullPallets() { return prioritizeFullPallets; }
    public void setPrioritizeFullPallets(boolean prioritizeFullPallets) { this.prioritizeFullPallets = prioritizeFullPallets; }

    public List<String> getCrossdockZones() { return crossdockZones; }
    public void setCrossdockZones(List<String> crossdockZones) { this.crossdockZones = crossdockZones; }

    public List<String> getStagingZones() { return stagingZones; }
    public void setStagingZones(List<String> stagingZones) { this.stagingZones = stagingZones; }

    public String getDefaultStagingZone() { return defaultStagingZone; }
    public void setDefaultStagingZone(String defaultStagingZone) { this.defaultStagingZone = defaultStagingZone; }

    public boolean isAutoAssignStaging() { return autoAssignStaging; }
    public void setAutoAssignStaging(boolean autoAssignStaging) { this.autoAssignStaging = autoAssignStaging; }

    public List<String> getAllowedSkuTypes() { return allowedSkuTypes; }
    public void setAllowedSkuTypes(List<String> allowedSkuTypes) { this.allowedSkuTypes = allowedSkuTypes; }

    public List<String> getExcludedSkuTypes() { return excludedSkuTypes; }
    public void setExcludedSkuTypes(List<String> excludedSkuTypes) { this.excludedSkuTypes = excludedSkuTypes; }

    public boolean isExcludeHazmat() { return excludeHazmat; }
    public void setExcludeHazmat(boolean excludeHazmat) { this.excludeHazmat = excludeHazmat; }

    public boolean isExcludeTemperatureControlled() { return excludeTemperatureControlled; }
    public void setExcludeTemperatureControlled(boolean excludeTemperatureControlled) { this.excludeTemperatureControlled = excludeTemperatureControlled; }

    public List<StorerKey> getAllowedStorers() { return allowedStorers; }
    public void setAllowedStorers(List<StorerKey> allowedStorers) { this.allowedStorers = allowedStorers; }

    public List<StorerKey> getExcludedStorers() { return excludedStorers; }
    public void setExcludedStorers(List<StorerKey> excludedStorers) { this.excludedStorers = excludedStorers; }

    public boolean isStorerOptIn() { return storerOptIn; }
    public void setStorerOptIn(boolean storerOptIn) { this.storerOptIn = storerOptIn; }

    public List<String> getAllowedOrderTypes() { return allowedOrderTypes; }
    public void setAllowedOrderTypes(List<String> allowedOrderTypes) { this.allowedOrderTypes = allowedOrderTypes; }

    public List<String> getExcludedOrderTypes() { return excludedOrderTypes; }
    public void setExcludedOrderTypes(List<String> excludedOrderTypes) { this.excludedOrderTypes = excludedOrderTypes; }

    public List<String> getAllowedCarriers() { return allowedCarriers; }
    public void setAllowedCarriers(List<String> allowedCarriers) { this.allowedCarriers = allowedCarriers; }

    public List<String> getAllowedRoutes() { return allowedRoutes; }
    public void setAllowedRoutes(List<String> allowedRoutes) { this.allowedRoutes = allowedRoutes; }

    public boolean isCheckTimeWindow() { return checkTimeWindow; }
    public void setCheckTimeWindow(boolean checkTimeWindow) { this.checkTimeWindow = checkTimeWindow; }

    public int getMinLeadTimeHours() { return minLeadTimeHours; }
    public void setMinLeadTimeHours(int minLeadTimeHours) { this.minLeadTimeHours = minLeadTimeHours; }

    public int getMaxLeadTimeHours() { return maxLeadTimeHours; }
    public void setMaxLeadTimeHours(int maxLeadTimeHours) { this.maxLeadTimeHours = maxLeadTimeHours; }

    public boolean isFallbackToStorage() { return fallbackToStorage; }
    public void setFallbackToStorage(boolean fallbackToStorage) { this.fallbackToStorage = fallbackToStorage; }

    public String getFallbackStrategyKey() { return fallbackStrategyKey; }
    public void setFallbackStrategyKey(String fallbackStrategyKey) { this.fallbackStrategyKey = fallbackStrategyKey; }

    public String getNoMatchAction() { return noMatchAction; }
    public void setNoMatchAction(String noMatchAction) { this.noMatchAction = noMatchAction; }

    public static class Builder {
        private final CrossdockStrategy strategy = new CrossdockStrategy();

        public Builder strategyKey(String key) { strategy.strategyKey = key; return this; }
        public Builder strategyName(String name) { strategy.strategyName = name; return this; }
        public Builder strategyType(CrossdockStrategyType type) { strategy.strategyType = type; return this; }
        public Builder active(boolean val) { strategy.active = val; return this; }
        public Builder matchBySku(boolean val) { strategy.matchBySku = val; return this; }
        public Builder matchByLot(boolean val) { strategy.matchByLot = val; return this; }
        public Builder matchByStorer(boolean val) { strategy.matchByStorer = val; return this; }
        public Builder matchByOrder(boolean val) { strategy.matchByOrder = val; return this; }
        public Builder allowPartialMatch(boolean val) { strategy.allowPartialMatch = val; return this; }
        public Builder demandHorizonDays(int days) { strategy.demandHorizonDays = days; return this; }

        public CrossdockStrategy build() {
            return strategy;
        }
    }
}
