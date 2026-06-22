package com.maersk.wms.inventory.domain;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Inventory Hold record.
 * Applies hold to specific inventory or entire lot/SKU.
 */
@Data
@Builder
public class InventoryHold {

    private String holdKey;

    /** Hold code (e.g., QC, DMG, RECALL) */
    private String holdCode;

    private String holdDescription;

    /** Scope of hold */
    private HoldScope scope;

    /** Filter criteria based on scope */
    private String sku;
    private String lot;
    private String location;
    private String lpn;

    /** Reason for hold */
    private String reasonCode;
    private String comments;

    /** Hold dates */
    private LocalDateTime holdDate;
    private LocalDateTime releaseDate;

    /** Who applied/released */
    private String holdBy;
    private String releaseBy;

    private boolean isActive;

    // Multi-tenant
    private String countryCode;
    private String clientCode;
    private String warehouseCode;
}
