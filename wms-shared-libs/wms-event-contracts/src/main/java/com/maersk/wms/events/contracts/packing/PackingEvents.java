package com.maersk.wms.events.contracts.packing;

import com.maersk.wms.events.contracts.BaseDomainEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Event contracts for Packing operations.
 * Published by: packing-operations-service
 * Consumed by: inventory-service, printing-service, shipping
 */
public final class PackingEvents {

    private PackingEvents() {}

    /**
     * Published when packing is started.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class PackingStarted extends BaseDomainEvent {
        private String packKey;
        private String orderKey;
        private String stationKey;
        private String packerKey;
        private Instant startedAt;
    }

    /**
     * Published when a carton is created.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class CartonCreated extends BaseDomainEvent {
        private String cartonKey;
        private String orderKey;
        private String cartonType;
        private String packKey;
    }

    /**
     * Published when an item is packed into a carton.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class ItemPacked extends BaseDomainEvent {
        private String cartonKey;
        private String orderKey;
        private String skuKey;
        private String inventoryKey;
        private BigDecimal quantity;
        private String packedBy;
    }

    /**
     * Published when a carton is closed.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class CartonClosed extends BaseDomainEvent {
        private String cartonKey;
        private String orderKey;
        private BigDecimal weight;
        private String weightUom;
        private String dimensions;
        private List<PackedItem> contents;
        private String closedBy;
        private Instant closedAt;
    }

    /**
     * Published when packing is completed.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class PackingCompleted extends BaseDomainEvent {
        private String packKey;
        private String orderKey;
        private List<String> cartonKeys;
        private int totalCartons;
        private BigDecimal totalWeight;
        private String completedBy;
        private Instant completedAt;
    }

    /**
     * Published when a pallet is built.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class PalletBuilt extends BaseDomainEvent {
        private String palletKey;
        private List<String> cartonKeys;
        private int cartonCount;
        private String locationKey;
        private String builtBy;
    }

    /**
     * Packed item detail.
     */
    @Data
    @NoArgsConstructor
    public static class PackedItem {
        private String skuKey;
        private String lotKey;
        private BigDecimal quantity;
    }
}
