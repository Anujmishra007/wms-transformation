package com.maersk.wms.masterdata.domain.product_master.event;

import com.maersk.wms.masterdata.shared.kernel.events.MasterDataDomainEvent;
import com.maersk.wms.masterdata.shared.kernel.identifiers.*;
import com.maersk.wms.masterdata.shared.kernel.valueobjects.*;

import java.time.Instant;

/**
 * Domain events for Product Master bounded context.
 */
public final class ProductMasterEvents {

    private ProductMasterEvents() {}

    // ==================== SKU Events ====================

    public record SkuCreated(
            SkuKey skuKey,
            StorerKey storerKey,
            String skuCode,
            String description,
            String skuGroup,
            String productClass,
            Instant createdAt,
            String createdBy
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return skuKey.value(); }
        @Override public String eventType() { return "SKU_CREATED"; }
        @Override public Instant occurredAt() { return createdAt; }
    }

    public record SkuUpdated(
            SkuKey skuKey,
            String fieldName,
            String oldValue,
            String newValue,
            Instant updatedAt,
            String updatedBy
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return skuKey.value(); }
        @Override public String eventType() { return "SKU_UPDATED"; }
        @Override public Instant occurredAt() { return updatedAt; }
    }

    public record SkuActivated(
            SkuKey skuKey,
            Instant activatedAt,
            String activatedBy
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return skuKey.value(); }
        @Override public String eventType() { return "SKU_ACTIVATED"; }
        @Override public Instant occurredAt() { return activatedAt; }
    }

    public record SkuDeactivated(
            SkuKey skuKey,
            String reason,
            Instant deactivatedAt,
            String deactivatedBy
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return skuKey.value(); }
        @Override public String eventType() { return "SKU_DEACTIVATED"; }
        @Override public Instant occurredAt() { return deactivatedAt; }
    }

    public record SkuDiscontinued(
            SkuKey skuKey,
            String reason,
            Instant discontinuedAt,
            String discontinuedBy
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return skuKey.value(); }
        @Override public String eventType() { return "SKU_DISCONTINUED"; }
        @Override public Instant occurredAt() { return discontinuedAt; }
    }

    // ==================== Dimension Events ====================

    public record DimensionAdded(
            SkuKey skuKey,
            DimensionKey dimensionKey,
            String packType,
            Dimensions dimensions,
            Instant addedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return skuKey.value(); }
        @Override public String eventType() { return "DIMENSION_ADDED"; }
        @Override public Instant occurredAt() { return addedAt; }
    }

    public record DimensionUpdated(
            DimensionKey dimensionKey,
            SkuKey skuKey,
            String packType,
            Dimensions oldDimensions,
            Dimensions newDimensions,
            Instant updatedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return dimensionKey.value(); }
        @Override public String eventType() { return "DIMENSION_UPDATED"; }
        @Override public Instant occurredAt() { return updatedAt; }
    }

    public record DimensionRemoved(
            DimensionKey dimensionKey,
            SkuKey skuKey,
            Instant removedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return dimensionKey.value(); }
        @Override public String eventType() { return "DIMENSION_REMOVED"; }
        @Override public Instant occurredAt() { return removedAt; }
    }

    // ==================== Lottable Events ====================

    public record LottableAdded(
            SkuKey skuKey,
            LottableKey lottableKey,
            String lottableField,
            String label,
            String dataType,
            Instant addedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return skuKey.value(); }
        @Override public String eventType() { return "LOTTABLE_ADDED"; }
        @Override public Instant occurredAt() { return addedAt; }
    }

    public record LottableUpdated(
            LottableKey lottableKey,
            SkuKey skuKey,
            String lottableField,
            Instant updatedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return lottableKey.value(); }
        @Override public String eventType() { return "LOTTABLE_UPDATED"; }
        @Override public Instant occurredAt() { return updatedAt; }
    }

    public record LottableRemoved(
            LottableKey lottableKey,
            SkuKey skuKey,
            Instant removedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return lottableKey.value(); }
        @Override public String eventType() { return "LOTTABLE_REMOVED"; }
        @Override public Instant occurredAt() { return removedAt; }
    }
}
