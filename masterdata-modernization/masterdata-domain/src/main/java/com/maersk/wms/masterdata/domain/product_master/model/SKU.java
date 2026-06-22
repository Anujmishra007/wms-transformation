package com.maersk.wms.masterdata.domain.product_master.model;

import com.maersk.wms.masterdata.shared.kernel.identifiers.*;
import com.maersk.wms.masterdata.shared.kernel.valueobjects.*;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * SKU (Stock Keeping Unit) aggregate root.
 * Represents a product in the warehouse master data.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SKU {

    private SkuKey skuKey;
    private StorerKey storerKey;
    private String skuDescription;
    private String altSkuCode;

    // Categorization
    private String skuGroup;
    private String productClass;
    private String productFamily;
    private String hazmatClass;
    private boolean hazardous;
    private boolean serialTracked;
    private boolean lotTracked;

    // Unit of Measures
    private String baseUom;
    private String packUom;
    private String caseUom;
    private String palletUom;
    private BigDecimal unitsPerPack;
    private BigDecimal packsPerCase;
    private BigDecimal casesPerPallet;

    // Dimensions (value object)
    private Dimensions dimensions;

    // Lottable Configuration (value object)
    private LottableConfig lottableConfig;

    // Storage
    private String storageProfile;
    private String putawayStrategy;
    private String rotationRule; // FIFO, FEFO, LIFO
    private BigDecimal abcVelocity;

    // Pricing
    private BigDecimal unitCost;
    private BigDecimal retailPrice;
    private String currency;

    // Status
    private SkuStatus status;

    // Child entities
    @Builder.Default
    private List<SKUDimension> packDimensions = new ArrayList<>();
    @Builder.Default
    private List<SKULottable> lottableDefinitions = new ArrayList<>();

    // Audit
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;

    public enum SkuStatus {
        ACTIVE, INACTIVE, DISCONTINUED, PENDING_APPROVAL
    }

    // Business Methods
    public void activate() {
        if (this.status == SkuStatus.DISCONTINUED) {
            throw new IllegalStateException("Cannot activate discontinued SKU");
        }
        this.status = SkuStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.status = SkuStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    public void discontinue() {
        this.status = SkuStatus.DISCONTINUED;
        this.updatedAt = Instant.now();
    }

    public void addPackDimension(SKUDimension dimension) {
        this.packDimensions.add(dimension);
        this.updatedAt = Instant.now();
    }

    public void addLottableDefinition(SKULottable lottable) {
        this.lottableDefinitions.add(lottable);
        this.updatedAt = Instant.now();
    }

    public BigDecimal getUnitsPerCase() {
        if (unitsPerPack == null || packsPerCase == null) {
            return null;
        }
        return unitsPerPack.multiply(packsPerCase);
    }

    public BigDecimal getUnitsPerPallet() {
        BigDecimal unitsPerCase = getUnitsPerCase();
        if (unitsPerCase == null || casesPerPallet == null) {
            return null;
        }
        return unitsPerCase.multiply(casesPerPallet);
    }

    public boolean requiresLotTracking() {
        return lotTracked || (lottableConfig != null && lottableConfig.hasAnyTracking());
    }

    public boolean isActive() {
        return status == SkuStatus.ACTIVE;
    }
}
