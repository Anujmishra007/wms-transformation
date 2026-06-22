package com.maersk.wms.masterdata.domain.warehouse_structure.event;

import com.maersk.wms.masterdata.shared.kernel.events.MasterDataDomainEvent;
import com.maersk.wms.masterdata.shared.kernel.identifiers.*;
import com.maersk.wms.masterdata.shared.kernel.valueobjects.*;
import com.maersk.wms.masterdata.domain.warehouse_structure.model.*;

import java.time.Instant;

/**
 * Domain events for Warehouse Structure bounded context.
 */
public final class WarehouseStructureEvents {

    private WarehouseStructureEvents() {}

    // ==================== Warehouse Events ====================

    public record WarehouseCreated(
            WarehouseKey warehouseKey,
            String warehouseCode,
            String warehouseName,
            Address address,
            Instant createdAt,
            String createdBy
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return warehouseKey.value(); }
        @Override public String eventType() { return "WAREHOUSE_CREATED"; }
        @Override public Instant occurredAt() { return createdAt; }
    }

    public record WarehouseActivated(
            WarehouseKey warehouseKey,
            Instant activatedAt,
            String activatedBy
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return warehouseKey.value(); }
        @Override public String eventType() { return "WAREHOUSE_ACTIVATED"; }
        @Override public Instant occurredAt() { return activatedAt; }
    }

    public record WarehouseDeactivated(
            WarehouseKey warehouseKey,
            String reason,
            Instant deactivatedAt,
            String deactivatedBy
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return warehouseKey.value(); }
        @Override public String eventType() { return "WAREHOUSE_DEACTIVATED"; }
        @Override public Instant occurredAt() { return deactivatedAt; }
    }

    // ==================== Zone Events ====================

    public record ZoneCreated(
            ZoneKey zoneKey,
            WarehouseKey warehouseKey,
            String zoneCode,
            String zoneName,
            Zone.ZoneType zoneType,
            Instant createdAt,
            String createdBy
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return zoneKey.value(); }
        @Override public String eventType() { return "ZONE_CREATED"; }
        @Override public Instant occurredAt() { return createdAt; }
    }

    public record ZoneActivated(
            ZoneKey zoneKey,
            Instant activatedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return zoneKey.value(); }
        @Override public String eventType() { return "ZONE_ACTIVATED"; }
        @Override public Instant occurredAt() { return activatedAt; }
    }

    public record ZoneDeactivated(
            ZoneKey zoneKey,
            String reason,
            Instant deactivatedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return zoneKey.value(); }
        @Override public String eventType() { return "ZONE_DEACTIVATED"; }
        @Override public Instant occurredAt() { return deactivatedAt; }
    }

    // ==================== Location Events ====================

    public record LocationCreated(
            LocationKey locationKey,
            WarehouseKey warehouseKey,
            ZoneKey zoneKey,
            String locationCode,
            Location.LocationType locationType,
            Instant createdAt,
            String createdBy
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return locationKey.value(); }
        @Override public String eventType() { return "LOCATION_CREATED"; }
        @Override public Instant occurredAt() { return createdAt; }
    }

    public record LocationActivated(
            LocationKey locationKey,
            Instant activatedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return locationKey.value(); }
        @Override public String eventType() { return "LOCATION_ACTIVATED"; }
        @Override public Instant occurredAt() { return activatedAt; }
    }

    public record LocationDeactivated(
            LocationKey locationKey,
            String reason,
            Instant deactivatedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return locationKey.value(); }
        @Override public String eventType() { return "LOCATION_DEACTIVATED"; }
        @Override public Instant occurredAt() { return deactivatedAt; }
    }

    public record LocationLocked(
            LocationKey locationKey,
            String reason,
            Instant lockedAt,
            String lockedBy
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return locationKey.value(); }
        @Override public String eventType() { return "LOCATION_LOCKED"; }
        @Override public Instant occurredAt() { return lockedAt; }
    }

    public record LocationUnlocked(
            LocationKey locationKey,
            Instant unlockedAt,
            String unlockedBy
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return locationKey.value(); }
        @Override public String eventType() { return "LOCATION_UNLOCKED"; }
        @Override public Instant occurredAt() { return unlockedAt; }
    }

    public record LocationCapacityUpdated(
            LocationKey locationKey,
            LocationCapacity oldCapacity,
            LocationCapacity newCapacity,
            Instant updatedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return locationKey.value(); }
        @Override public String eventType() { return "LOCATION_CAPACITY_UPDATED"; }
        @Override public Instant occurredAt() { return updatedAt; }
    }

    // ==================== Aisle Events ====================

    public record AisleCreated(
            AisleKey aisleKey,
            WarehouseKey warehouseKey,
            ZoneKey zoneKey,
            String aisleCode,
            Instant createdAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return aisleKey.value(); }
        @Override public String eventType() { return "AISLE_CREATED"; }
        @Override public Instant occurredAt() { return createdAt; }
    }

    // ==================== Bay Events ====================

    public record BayCreated(
            BayKey bayKey,
            AisleKey aisleKey,
            String bayCode,
            Instant createdAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return bayKey.value(); }
        @Override public String eventType() { return "BAY_CREATED"; }
        @Override public Instant occurredAt() { return createdAt; }
    }

    // ==================== Level Events ====================

    public record LevelCreated(
            LevelKey levelKey,
            BayKey bayKey,
            String levelCode,
            int levelNumber,
            Instant createdAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return levelKey.value(); }
        @Override public String eventType() { return "LEVEL_CREATED"; }
        @Override public Instant occurredAt() { return createdAt; }
    }
}
