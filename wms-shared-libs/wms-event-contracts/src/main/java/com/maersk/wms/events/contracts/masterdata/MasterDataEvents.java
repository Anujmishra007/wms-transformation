package com.maersk.wms.events.contracts.masterdata;

import com.maersk.wms.events.contracts.BaseDomainEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Event contracts for Master Data operations.
 * Published by: master-data-service
 * Consumed by: All other microservices (for cache invalidation)
 */
public final class MasterDataEvents {

    private MasterDataEvents() {}

    /**
     * Published when a SKU is created or updated.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class SkuChanged extends BaseDomainEvent {
        private String skuKey;
        private String skuCode;
        private String storerKey;
        private String description;
        private String changeType; // CREATED, UPDATED, DELETED
    }

    /**
     * Published when a location is created or updated.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class LocationChanged extends BaseDomainEvent {
        private String locationKey;
        private String locationCode;
        private String zoneKey;
        private String locationType;
        private boolean active;
        private String changeType;
    }

    /**
     * Published when a storer is created or updated.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class StorerChanged extends BaseDomainEvent {
        private String storerKey;
        private String storerCode;
        private String storerName;
        private boolean active;
        private String changeType;
    }

    /**
     * Published when location capacity changes.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class LocationCapacityChanged extends BaseDomainEvent {
        private String locationKey;
        private BigDecimal maxWeight;
        private BigDecimal maxCube;
        private int maxPallets;
    }
}
