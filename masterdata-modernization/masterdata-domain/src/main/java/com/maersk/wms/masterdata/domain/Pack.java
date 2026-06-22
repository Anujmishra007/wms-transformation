package com.maersk.wms.masterdata.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pack/Packaging master data entity.
 * Maps to the PACK table in the WMS database.
 */
@Data
public class Pack {

    private Long id;
    private String packCode;
    private String description;
    private PackType packType;
    private PackStatus status;

    // Dimensions (outer)
    private BigDecimal outerLength;
    private BigDecimal outerWidth;
    private BigDecimal outerHeight;
    private String dimensionUom;

    // Dimensions (inner - usable space)
    private BigDecimal innerLength;
    private BigDecimal innerWidth;
    private BigDecimal innerHeight;

    // Weight
    private BigDecimal tareWeight;
    private BigDecimal maxGrossWeight;
    private String weightUom;

    // Volume
    private BigDecimal cube;
    private String cubeUom;
    private BigDecimal maxFillPercent;

    // Capacity
    private int maxItems;
    private int maxCases;
    private int maxEaches;

    // Carton characteristics
    private boolean standardCarton;
    private boolean gaylord;
    private boolean pallet;
    private boolean reusable;
    private boolean fragile;

    // Cost
    private BigDecimal cost;
    private String costCurrency;

    // Supplier
    private String supplierCode;
    private String supplierPartNumber;

    // Barcode
    private String barcode;
    private String ssccPrefix;

    // Custom
    private String customField01;
    private String customField02;

    // Audit
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
