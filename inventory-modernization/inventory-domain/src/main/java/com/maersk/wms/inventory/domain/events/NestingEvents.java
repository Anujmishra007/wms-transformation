package com.maersk.wms.inventory.domain.events;

import com.maersk.wms.inventory.shared.kernel.events.InventoryDomainEvent;
import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;
import com.maersk.wms.inventory.domain.structure.model.InventoryHierarchy;

import java.time.Instant;
import java.util.List;

/**
 * Domain events for inventory nesting/hierarchy operations.
 */
public final class NestingEvents {

    private NestingEvents() {}

    public record InventoryNested(
            NestingKey nestingKey,
            LpnKey parentLpnKey,
            InventoryHierarchy.ContainerType parentType,
            LpnKey childLpnKey,
            InventoryHierarchy.ContainerType childType,
            int quantity,
            LocationKey locationKey,
            UserKey nestedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.nested";
        }
    }

    public record InventoryUnnested(
            NestingKey nestingKey,
            LpnKey parentLpnKey,
            LpnKey childLpnKey,
            String unnestReason,
            LocationKey newLocationKey,
            UserKey unnestedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.unnested";
        }
    }

    public record PalletBuilt(
            LpnKey palletLpnKey,
            List<LpnKey> childLpnKeys,
            int totalCases,
            Quantity totalQuantity,
            LocationKey locationKey,
            UserKey builtBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.pallet_built";
        }
    }

    public record PalletBroken(
            LpnKey palletLpnKey,
            List<LpnKey> childLpnKeys,
            String breakReason,
            UserKey brokenBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.pallet_broken";
        }
    }

    public record ContainerConsolidated(
            LpnKey targetLpnKey,
            List<LpnKey> sourceLpnKeys,
            int itemsConsolidated,
            Quantity totalQuantity,
            UserKey consolidatedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.container_consolidated";
        }
    }

    public record MixedContainerCreated(
            LpnKey containerLpnKey,
            List<SkuKey> skusInContainer,
            int totalLpns,
            Quantity totalQuantity,
            LocationKey locationKey,
            UserKey createdBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.mixed_container_created";
        }
    }
}
