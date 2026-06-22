package com.maersk.wms.inventory.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Inventory Transfer between locations.
 * Used for replenishment, moves, and consolidation.
 */
@Data
@Builder
public class InventoryTransfer {

    private String transferKey;

    @NotBlank(message = "SKU is required")
    private String sku;

    private String lot;

    @NotBlank(message = "From location is required")
    private String fromLocation;

    private String fromLpn;

    @NotBlank(message = "To location is required")
    private String toLocation;

    private String toLpn;

    @NotNull(message = "Transfer quantity is required")
    @Positive(message = "Transfer quantity must be positive")
    private BigDecimal transferQty;

    @NotNull(message = "Transfer type is required")
    private TransferType transferType;

    private TransferStatus status;

    /** Reference task/order */
    private String referenceType;
    private String referenceKey;

    /** User performing transfer */
    private String userId;

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    // Multi-tenant
    private String countryCode;
    private String clientCode;
    private String warehouseCode;
}
