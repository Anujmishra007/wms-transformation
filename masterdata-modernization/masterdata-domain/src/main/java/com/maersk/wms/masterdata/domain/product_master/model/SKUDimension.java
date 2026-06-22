package com.maersk.wms.masterdata.domain.product_master.model;

import com.maersk.wms.masterdata.shared.kernel.identifiers.*;
import com.maersk.wms.masterdata.shared.kernel.valueobjects.Dimensions;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * SKU Dimension entity representing pack-level dimensions.
 * A SKU can have different dimensions for different pack types (EACH, CASE, PALLET).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SKUDimension {

    private DimensionKey dimensionKey;
    private SkuKey skuKey;

    // Pack Type
    private String packType; // EACH, INNER, CASE, PALLET
    private String uom;
    private BigDecimal conversionFactor; // Units per this pack

    // Physical Dimensions
    private Dimensions dimensions;

    // Cube/Weight
    private BigDecimal cube; // Volume in cubic meters
    private BigDecimal grossWeight;
    private BigDecimal netWeight;
    private BigDecimal tareWeight;

    // Container Info
    private int ti; // Tier (layers per pallet)
    private int hi; // High (stacking height)
    private int casesPerLayer;
    private int layersPerPallet;

    // Nesting
    private boolean nestable;
    private BigDecimal nestedHeight;

    // Status
    private boolean active;

    // Audit
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;

    // Business Methods
    public BigDecimal calculateVolume() {
        if (dimensions != null) {
            return dimensions.volume();
        }
        return cube;
    }

    public int calculateCasesPerPallet() {
        if (ti > 0 && hi > 0) {
            return ti * hi;
        }
        if (casesPerLayer > 0 && layersPerPallet > 0) {
            return casesPerLayer * layersPerPallet;
        }
        return 0;
    }

    public void activate() {
        this.active = true;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }
}
