package com.maersk.wms.masterdata.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Item/SKU master data entity.
 * Maps to the SKU table in the WMS database.
 */
@Data
public class Item {

    private Long id;
    private String sku;
    private String description;
    private String descriptionLong;

    // Item classification
    private ItemType itemType;
    private ItemStatus status;
    private String itemGroup;
    private String itemClass;
    private String itemFamily;

    // Physical attributes
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private String dimensionUom;
    private BigDecimal weight;
    private String weightUom;
    private BigDecimal cube;
    private String cubeUom;

    // Storage attributes
    private String storageType;
    private String storageZone;
    private String putawayZone;
    private String pickingZone;
    private String abcClass;
    private String velocityCode;
    private boolean conveyable;
    private boolean stackable;
    private int maxStackHeight;

    // Inventory control
    private boolean lotControlled;
    private boolean serialControlled;
    private boolean expirationControlled;
    private int shelfLife;
    private int minimumShelfLife;
    private String rotationRule;

    // Units of measure
    private String baseUom;
    private BigDecimal baseQtyPerPack;
    private BigDecimal baseQtyPerCase;
    private BigDecimal baseQtyPerPallet;

    // Lottable attributes
    private String lottable01Label;
    private String lottable02Label;
    private String lottable03Label;
    private String lottable04Label;
    private String lottable05Label;
    private boolean lottable01Required;
    private boolean lottable02Required;

    // Catch weight
    private boolean catchWeight;
    private BigDecimal minWeight;
    private BigDecimal maxWeight;
    private BigDecimal nominalWeight;

    // Hazmat
    private boolean hazmat;
    private String hazmatClass;
    private String unNumber;

    // Custom fields
    private String customField01;
    private String customField02;
    private String customField03;

    // Audit
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
