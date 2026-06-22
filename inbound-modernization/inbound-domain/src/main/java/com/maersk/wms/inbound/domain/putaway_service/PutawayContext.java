package com.maersk.wms.inbound.domain.putaway_service;

import com.maersk.wms.inbound.shared.kernel.identifiers.LocationKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.LpnKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.SkuKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;
import com.maersk.wms.inbound.shared.kernel.valueobjects.Quantity;

import java.time.LocalDate;
import java.util.List;

/**
 * Context object for putaway algorithm execution.
 * Contains all information needed to determine optimal putaway location.
 */
public class PutawayContext {

    private StorerKey storerKey;
    private SkuKey skuKey;
    private LpnKey lpnKey;
    private Quantity quantity;
    private String lot;
    private LocalDate expiryDate;
    private LocalDate receiptDate;

    // SKU attributes
    private String velocityClass; // A, B, C
    private String skuType;
    private boolean hazmat;
    private boolean temperatureControlled;
    private String temperatureZone;

    // Strategy/Algorithm reference
    private String strategyKey;
    private String algorithmKey;

    // Zone constraints
    private List<String> allowedZones;
    private List<String> excludedZones;
    private String preferredZone;

    // Location type constraints
    private List<String> allowedLocationTypes;
    private String preferredLocationType;

    // Source info
    private LocationKey fromLocation;
    private String receiptKey;
    private String putawayTaskKey;
    private boolean isReturn;
    private String disposition;
    private boolean isCrossdock;
    private String orderKey;

    // Optimization hints
    private boolean preferConsolidation;
    private boolean preferEmpty;
    private int maxLocationsToEvaluate;

    public PutawayContext() {}

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public StorerKey getStorerKey() { return storerKey; }
    public void setStorerKey(StorerKey storerKey) { this.storerKey = storerKey; }

    public SkuKey getSkuKey() { return skuKey; }
    public void setSkuKey(SkuKey skuKey) { this.skuKey = skuKey; }

    public LpnKey getLpnKey() { return lpnKey; }
    public void setLpnKey(LpnKey lpnKey) { this.lpnKey = lpnKey; }

    public Quantity getQuantity() { return quantity; }
    public void setQuantity(Quantity quantity) { this.quantity = quantity; }

    public String getLot() { return lot; }
    public void setLot(String lot) { this.lot = lot; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public LocalDate getReceiptDate() { return receiptDate; }
    public void setReceiptDate(LocalDate receiptDate) { this.receiptDate = receiptDate; }

    public String getVelocityClass() { return velocityClass; }
    public void setVelocityClass(String velocityClass) { this.velocityClass = velocityClass; }

    public String getSkuType() { return skuType; }
    public void setSkuType(String skuType) { this.skuType = skuType; }

    public boolean isHazmat() { return hazmat; }
    public void setHazmat(boolean hazmat) { this.hazmat = hazmat; }

    public boolean isTemperatureControlled() { return temperatureControlled; }
    public void setTemperatureControlled(boolean temperatureControlled) { this.temperatureControlled = temperatureControlled; }

    public String getTemperatureZone() { return temperatureZone; }
    public void setTemperatureZone(String temperatureZone) { this.temperatureZone = temperatureZone; }

    public String getStrategyKey() { return strategyKey; }
    public void setStrategyKey(String strategyKey) { this.strategyKey = strategyKey; }

    public String getAlgorithmKey() { return algorithmKey; }
    public void setAlgorithmKey(String algorithmKey) { this.algorithmKey = algorithmKey; }

    public List<String> getAllowedZones() { return allowedZones; }
    public void setAllowedZones(List<String> allowedZones) { this.allowedZones = allowedZones; }

    public List<String> getExcludedZones() { return excludedZones; }
    public void setExcludedZones(List<String> excludedZones) { this.excludedZones = excludedZones; }

    public String getPreferredZone() { return preferredZone; }
    public void setPreferredZone(String preferredZone) { this.preferredZone = preferredZone; }

    public List<String> getAllowedLocationTypes() { return allowedLocationTypes; }
    public void setAllowedLocationTypes(List<String> allowedLocationTypes) { this.allowedLocationTypes = allowedLocationTypes; }

    public String getPreferredLocationType() { return preferredLocationType; }
    public void setPreferredLocationType(String preferredLocationType) { this.preferredLocationType = preferredLocationType; }

    public LocationKey getFromLocation() { return fromLocation; }
    public void setFromLocation(LocationKey fromLocation) { this.fromLocation = fromLocation; }

    public String getReceiptKey() { return receiptKey; }
    public void setReceiptKey(String receiptKey) { this.receiptKey = receiptKey; }

    public String getPutawayTaskKey() { return putawayTaskKey; }
    public void setPutawayTaskKey(String putawayTaskKey) { this.putawayTaskKey = putawayTaskKey; }

    public boolean isReturn() { return isReturn; }
    public void setReturn(boolean isReturn) { this.isReturn = isReturn; }

    public String getDisposition() { return disposition; }
    public void setDisposition(String disposition) { this.disposition = disposition; }

    public boolean isCrossdock() { return isCrossdock; }
    public void setCrossdock(boolean crossdock) { isCrossdock = crossdock; }

    public String getOrderKey() { return orderKey; }
    public void setOrderKey(String orderKey) { this.orderKey = orderKey; }

    public boolean isPreferConsolidation() { return preferConsolidation; }
    public void setPreferConsolidation(boolean preferConsolidation) { this.preferConsolidation = preferConsolidation; }

    public boolean isPreferEmpty() { return preferEmpty; }
    public void setPreferEmpty(boolean preferEmpty) { this.preferEmpty = preferEmpty; }

    public int getMaxLocationsToEvaluate() { return maxLocationsToEvaluate; }
    public void setMaxLocationsToEvaluate(int maxLocationsToEvaluate) { this.maxLocationsToEvaluate = maxLocationsToEvaluate; }

    public static class Builder {
        private final PutawayContext context = new PutawayContext();

        public Builder storerKey(StorerKey key) { context.storerKey = key; return this; }
        public Builder skuKey(SkuKey key) { context.skuKey = key; return this; }
        public Builder lpnKey(LpnKey key) { context.lpnKey = key; return this; }
        public Builder quantity(Quantity qty) { context.quantity = qty; return this; }
        public Builder lot(String lot) { context.lot = lot; return this; }
        public Builder expiryDate(LocalDate date) { context.expiryDate = date; return this; }
        public Builder velocityClass(String vc) { context.velocityClass = vc; return this; }
        public Builder allowedZones(List<String> zones) { context.allowedZones = zones; return this; }
        public Builder fromLocation(LocationKey loc) { context.fromLocation = loc; return this; }
        public Builder receiptKey(String key) { context.receiptKey = key; return this; }
        public Builder isReturn(boolean val) { context.isReturn = val; return this; }
        public Builder disposition(String disp) { context.disposition = disp; return this; }
        public Builder isCrossdock(boolean val) { context.isCrossdock = val; return this; }
        public Builder orderKey(String key) { context.orderKey = key; return this; }

        public PutawayContext build() {
            return context;
        }
    }
}
