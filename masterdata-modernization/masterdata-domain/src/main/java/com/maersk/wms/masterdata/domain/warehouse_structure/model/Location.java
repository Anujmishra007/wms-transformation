package com.maersk.wms.masterdata.domain.warehouse_structure.model;

import com.maersk.wms.masterdata.shared.kernel.identifiers.*;
import com.maersk.wms.masterdata.shared.kernel.valueobjects.*;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Location entity representing a storage location in the warehouse.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    private LocationKey locationKey;
    private WarehouseKey warehouseKey;
    private ZoneKey zoneKey;
    private AisleKey aisleKey;
    private BayKey bayKey;
    private LevelKey levelKey;

    // Location Identification
    private String locationCode;
    private String barcode;
    private String checkDigit;

    // Location Type
    private LocationType locationType;
    private String storageType; // RACK, FLOOR, BULK, MEZZANINE
    private String handlingType; // STANDARD, FORKLIFT, MANUAL

    // Position
    private String aisle;
    private String bay;
    private String level;
    private String position;
    private int xCoordinate;
    private int yCoordinate;
    private int zCoordinate;
    private int pickSequence;

    // Capacity
    private LocationCapacity capacity;
    private BigDecimal currentWeight;
    private BigDecimal currentVolume;
    private int currentPallets;

    // Dimensions
    private Dimensions dimensions;

    // Configuration
    private String abcClass; // A, B, C velocity
    private String putawayZone;
    private String pickZone;
    private boolean multiSku; // Can hold multiple SKUs
    private boolean mixedLot; // Can hold mixed lots

    // Restrictions
    private String storerRestriction; // Specific storer only
    private String skuRestriction; // Specific SKU only
    private String productClassRestriction;

    // Status
    private LocationStatus status;
    private boolean available;
    private boolean locked;
    private String lockReason;

    // Audit
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;

    public enum LocationType {
        STORAGE, STAGING, RECEIVING, SHIPPING, DOCK, DAMAGED, RETURNS, QUALITY, KITTING, PACK
    }

    public enum LocationStatus {
        ACTIVE, INACTIVE, BLOCKED, UNDER_MAINTENANCE
    }

    // Business Methods
    public void activate() {
        this.status = LocationStatus.ACTIVE;
        this.available = true;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.status = LocationStatus.INACTIVE;
        this.available = false;
        this.updatedAt = Instant.now();
    }

    public void lock(String reason) {
        this.locked = true;
        this.lockReason = reason;
        this.available = false;
        this.updatedAt = Instant.now();
    }

    public void unlock() {
        this.locked = false;
        this.lockReason = null;
        this.available = status == LocationStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public boolean canAcceptWeight(BigDecimal weight) {
        if (capacity == null) return true;
        BigDecimal available = capacity.maxWeight().subtract(
                currentWeight != null ? currentWeight : BigDecimal.ZERO);
        return available.compareTo(weight) >= 0;
    }

    public boolean canAcceptVolume(BigDecimal volume) {
        if (capacity == null) return true;
        BigDecimal available = capacity.maxVolume().subtract(
                currentVolume != null ? currentVolume : BigDecimal.ZERO);
        return available.compareTo(volume) >= 0;
    }

    public boolean canAcceptPallet() {
        if (capacity == null) return true;
        return currentPallets < capacity.maxPallets();
    }

    public boolean isEmpty() {
        return (currentWeight == null || currentWeight.compareTo(BigDecimal.ZERO) == 0) &&
               (currentVolume == null || currentVolume.compareTo(BigDecimal.ZERO) == 0) &&
               currentPallets == 0;
    }

    public String getFullLocationCode() {
        StringBuilder sb = new StringBuilder();
        if (aisle != null) sb.append(aisle);
        if (bay != null) sb.append("-").append(bay);
        if (level != null) sb.append("-").append(level);
        if (position != null) sb.append("-").append(position);
        return sb.length() > 0 ? sb.toString() : locationCode;
    }
}
