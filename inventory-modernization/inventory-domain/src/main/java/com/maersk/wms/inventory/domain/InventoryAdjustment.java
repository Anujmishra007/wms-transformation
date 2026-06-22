package com.maersk.wms.inventory.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Inventory Adjustment request.
 * Used for cycle count adjustments, damage write-offs, etc.
 */
@Data
@Builder
public class InventoryAdjustment {

    private String adjustmentKey;

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotBlank(message = "Location is required")
    private String location;

    private String lpn;
    private String lot;

    @NotNull(message = "Adjustment type is required")
    private AdjustmentType adjustmentType;

    /** Current system quantity */
    private BigDecimal systemQty;

    /** Actual counted/adjusted quantity */
    @NotNull(message = "Adjusted quantity is required")
    private BigDecimal adjustedQty;

    /** Variance = adjustedQty - systemQty */
    public BigDecimal getVariance() {
        if (systemQty == null) return adjustedQty;
        return adjustedQty.subtract(systemQty);
    }

    @NotBlank(message = "Reason code is required")
    private String reasonCode;

    private String comments;

    /** Reference document (cycle count, receipt, etc.) */
    private String referenceType;
    private String referenceKey;

    /** User who performed adjustment */
    private String userId;

    /** Supervisor approval if required */
    private String supervisorId;
    private boolean supervisorApproved;

    private LocalDateTime adjustmentDate;

    /** Status of adjustment */
    private AdjustmentStatus status;

    // Multi-tenant
    private String countryCode;
    private String clientCode;
    private String warehouseCode;
}
