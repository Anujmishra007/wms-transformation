package com.maersk.wms.masterdata.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Unit of Measure master data entity.
 * Maps to the UOM table in the WMS database.
 */
@Data
public class UnitOfMeasure {

    private Long id;
    private String uomCode;
    private String description;
    private UomType uomType;
    private boolean active;

    // Base conversion
    private String baseUom;
    private BigDecimal conversionFactor;

    // Dimensional UOM
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private String dimensionUom;
    private BigDecimal weight;
    private String weightUom;
    private BigDecimal cube;
    private String cubeUom;

    // Quantity per
    private int qtyPerInnerPack;
    private int qtyPerOuterPack;
    private int qtyPerCase;
    private int qtyPerPallet;
    private int tiHi; // Pallet configuration (e.g., "4x5" = Ti 4, Hi 5)
    private int ti;
    private int hi;

    // Labeling
    private boolean labelRequired;
    private String defaultLabelFormat;
    private String barcodeFormat;

    // Pricing
    private boolean pricingUom;
    private BigDecimal defaultPrice;
    private String priceCurrency;

    // Handling
    private boolean pickable;
    private boolean receivable;
    private boolean shippable;
    private boolean storageUom;

    // Audit
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
