package com.maersk.wms.inbound.domain.putaway_service;

import com.maersk.wms.inbound.shared.kernel.identifiers.LocationKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.SkuKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;
import com.maersk.wms.inbound.shared.kernel.valueobjects.Quantity;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Location Allocation entity for putaway-service subdomain.
 * Represents an allocated location for putaway or storage.
 * Tracks location capacity, reservations, and allocation status.
 *
 * Legacy Table: LOC, LOCXLOCTYPE, PUTAWAYLOCATION
 * Legacy SPs: nsp_GetPutawayLocation, nsp_AllocateLocation, nsp_DirectedPutaway
 */
public class LocationAllocation {

    private String allocationKey;
    private LocationKey locationKey;
    private StorerKey storerKey;
    private SkuKey skuKey;
    private String putawayKey;
    private AllocationStatus status;
    private AllocationType allocationType;

    // Capacity tracking
    private BigDecimal totalCapacity;
    private BigDecimal usedCapacity;
    private BigDecimal allocatedCapacity;
    private BigDecimal availableCapacity;
    private String capacityUom;

    // Quantity tracking
    private Quantity allocatedQty;
    private Quantity actualQty;
    private Quantity maxQty;

    // Location attributes
    private String zone;
    private String locationType;
    private String aisle;
    private String bay;
    private String level;
    private String position;
    private int sequence;
    private int pickSequence;

    // SKU constraints
    private boolean mixedSkuAllowed;
    private boolean mixedLotAllowed;
    private int currentSkuCount;
    private int maxSkuCount;
    private int currentLotCount;
    private int maxLotCount;

    // Priority and scoring
    private int priority;
    private BigDecimal score;
    private String scoreReason;

    // Time tracking
    private Instant allocatedAt;
    private Instant confirmedAt;
    private Instant releasedAt;
    private Instant expiresAt;
    private String allocatedBy;
    private String confirmedBy;

    // Metadata
    private String reasonCode;
    private String notes;
    private int trafficCop;

    public LocationAllocation() {}

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    // Domain methods

    public void reserve(Quantity qty) {
        if (this.status != AllocationStatus.AVAILABLE) {
            throw new IllegalStateException("Location not available for reservation");
        }
        this.allocatedQty = qty;
        this.status = AllocationStatus.RESERVED;
        this.allocatedAt = Instant.now();
    }

    public void confirm(Quantity actualQty, String userId) {
        if (this.status != AllocationStatus.RESERVED) {
            throw new IllegalStateException("Can only confirm reserved allocations");
        }
        this.actualQty = actualQty;
        this.status = AllocationStatus.CONFIRMED;
        this.confirmedAt = Instant.now();
        this.confirmedBy = userId;
        updateCapacity(actualQty);
    }

    public void release(String reason) {
        this.status = AllocationStatus.RELEASED;
        this.releasedAt = Instant.now();
        this.reasonCode = reason;
    }

    public void expire() {
        if (this.status == AllocationStatus.RESERVED) {
            this.status = AllocationStatus.EXPIRED;
            this.releasedAt = Instant.now();
            this.reasonCode = "EXPIRED";
        }
    }

    public void reject(String reason) {
        this.status = AllocationStatus.REJECTED;
        this.releasedAt = Instant.now();
        this.reasonCode = reason;
    }

    private void updateCapacity(Quantity qty) {
        // Update capacity calculations
        BigDecimal qtyValue = qty != null ? qty.getValue() : BigDecimal.ZERO;
        this.usedCapacity = this.usedCapacity.add(qtyValue);
        this.availableCapacity = this.totalCapacity.subtract(this.usedCapacity).subtract(this.allocatedCapacity);
    }

    public boolean hasCapacityFor(Quantity qty) {
        if (availableCapacity == null || qty == null) return false;
        return availableCapacity.compareTo(qty.getValue()) >= 0;
    }

    public boolean canAcceptSku(SkuKey sku) {
        if (this.currentSkuCount == 0) return true;
        if (this.skuKey != null && this.skuKey.equals(sku)) return true;
        return this.mixedSkuAllowed && this.currentSkuCount < this.maxSkuCount;
    }

    public boolean canAcceptLot(String lot) {
        if (this.currentLotCount == 0) return true;
        return this.mixedLotAllowed && this.currentLotCount < this.maxLotCount;
    }

    public boolean isExpired() {
        if (this.expiresAt == null) return false;
        return Instant.now().isAfter(this.expiresAt);
    }

    public boolean isAvailable() {
        return this.status == AllocationStatus.AVAILABLE;
    }

    public boolean isReserved() {
        return this.status == AllocationStatus.RESERVED;
    }

    public BigDecimal getUtilizationPercent() {
        if (totalCapacity == null || totalCapacity.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return usedCapacity.divide(totalCapacity, 4, java.math.RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
    }

    // Getters and Setters
    public String getAllocationKey() { return allocationKey; }
    public void setAllocationKey(String allocationKey) { this.allocationKey = allocationKey; }

    public LocationKey getLocationKey() { return locationKey; }
    public void setLocationKey(LocationKey locationKey) { this.locationKey = locationKey; }

    public StorerKey getStorerKey() { return storerKey; }
    public void setStorerKey(StorerKey storerKey) { this.storerKey = storerKey; }

    public SkuKey getSkuKey() { return skuKey; }
    public void setSkuKey(SkuKey skuKey) { this.skuKey = skuKey; }

    public String getPutawayKey() { return putawayKey; }
    public void setPutawayKey(String putawayKey) { this.putawayKey = putawayKey; }

    public AllocationStatus getStatus() { return status; }
    public void setStatus(AllocationStatus status) { this.status = status; }

    public AllocationType getAllocationType() { return allocationType; }
    public void setAllocationType(AllocationType allocationType) { this.allocationType = allocationType; }

    public BigDecimal getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(BigDecimal totalCapacity) { this.totalCapacity = totalCapacity; }

    public BigDecimal getUsedCapacity() { return usedCapacity; }
    public void setUsedCapacity(BigDecimal usedCapacity) { this.usedCapacity = usedCapacity; }

    public BigDecimal getAllocatedCapacity() { return allocatedCapacity; }
    public void setAllocatedCapacity(BigDecimal allocatedCapacity) { this.allocatedCapacity = allocatedCapacity; }

    public BigDecimal getAvailableCapacity() { return availableCapacity; }
    public void setAvailableCapacity(BigDecimal availableCapacity) { this.availableCapacity = availableCapacity; }

    public String getCapacityUom() { return capacityUom; }
    public void setCapacityUom(String capacityUom) { this.capacityUom = capacityUom; }

    public Quantity getAllocatedQty() { return allocatedQty; }
    public void setAllocatedQty(Quantity allocatedQty) { this.allocatedQty = allocatedQty; }

    public Quantity getActualQty() { return actualQty; }
    public void setActualQty(Quantity actualQty) { this.actualQty = actualQty; }

    public Quantity getMaxQty() { return maxQty; }
    public void setMaxQty(Quantity maxQty) { this.maxQty = maxQty; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public String getLocationType() { return locationType; }
    public void setLocationType(String locationType) { this.locationType = locationType; }

    public String getAisle() { return aisle; }
    public void setAisle(String aisle) { this.aisle = aisle; }

    public String getBay() { return bay; }
    public void setBay(String bay) { this.bay = bay; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public int getSequence() { return sequence; }
    public void setSequence(int sequence) { this.sequence = sequence; }

    public int getPickSequence() { return pickSequence; }
    public void setPickSequence(int pickSequence) { this.pickSequence = pickSequence; }

    public boolean isMixedSkuAllowed() { return mixedSkuAllowed; }
    public void setMixedSkuAllowed(boolean mixedSkuAllowed) { this.mixedSkuAllowed = mixedSkuAllowed; }

    public boolean isMixedLotAllowed() { return mixedLotAllowed; }
    public void setMixedLotAllowed(boolean mixedLotAllowed) { this.mixedLotAllowed = mixedLotAllowed; }

    public int getCurrentSkuCount() { return currentSkuCount; }
    public void setCurrentSkuCount(int currentSkuCount) { this.currentSkuCount = currentSkuCount; }

    public int getMaxSkuCount() { return maxSkuCount; }
    public void setMaxSkuCount(int maxSkuCount) { this.maxSkuCount = maxSkuCount; }

    public int getCurrentLotCount() { return currentLotCount; }
    public void setCurrentLotCount(int currentLotCount) { this.currentLotCount = currentLotCount; }

    public int getMaxLotCount() { return maxLotCount; }
    public void setMaxLotCount(int maxLotCount) { this.maxLotCount = maxLotCount; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }

    public String getScoreReason() { return scoreReason; }
    public void setScoreReason(String scoreReason) { this.scoreReason = scoreReason; }

    public Instant getAllocatedAt() { return allocatedAt; }
    public void setAllocatedAt(Instant allocatedAt) { this.allocatedAt = allocatedAt; }

    public Instant getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(Instant confirmedAt) { this.confirmedAt = confirmedAt; }

    public Instant getReleasedAt() { return releasedAt; }
    public void setReleasedAt(Instant releasedAt) { this.releasedAt = releasedAt; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public String getAllocatedBy() { return allocatedBy; }
    public void setAllocatedBy(String allocatedBy) { this.allocatedBy = allocatedBy; }

    public String getConfirmedBy() { return confirmedBy; }
    public void setConfirmedBy(String confirmedBy) { this.confirmedBy = confirmedBy; }

    public String getReasonCode() { return reasonCode; }
    public void setReasonCode(String reasonCode) { this.reasonCode = reasonCode; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public int getTrafficCop() { return trafficCop; }
    public void setTrafficCop(int trafficCop) { this.trafficCop = trafficCop; }

    public static class Builder {
        private final LocationAllocation allocation = new LocationAllocation();

        public Builder allocationKey(String key) { allocation.allocationKey = key; return this; }
        public Builder locationKey(LocationKey key) { allocation.locationKey = key; return this; }
        public Builder storerKey(StorerKey key) { allocation.storerKey = key; return this; }
        public Builder skuKey(SkuKey key) { allocation.skuKey = key; return this; }
        public Builder zone(String zone) { allocation.zone = zone; return this; }
        public Builder locationType(String type) { allocation.locationType = type; return this; }
        public Builder totalCapacity(BigDecimal cap) { allocation.totalCapacity = cap; return this; }
        public Builder mixedSkuAllowed(boolean val) { allocation.mixedSkuAllowed = val; return this; }
        public Builder mixedLotAllowed(boolean val) { allocation.mixedLotAllowed = val; return this; }
        public Builder priority(int priority) { allocation.priority = priority; return this; }

        public LocationAllocation build() {
            allocation.status = AllocationStatus.AVAILABLE;
            allocation.usedCapacity = BigDecimal.ZERO;
            allocation.allocatedCapacity = BigDecimal.ZERO;
            allocation.availableCapacity = allocation.totalCapacity;
            allocation.currentSkuCount = 0;
            allocation.currentLotCount = 0;
            return allocation;
        }
    }
}
