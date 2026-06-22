package com.maersk.wms.masterdata.domain.product_master.model;

import com.maersk.wms.masterdata.shared.kernel.identifiers.*;

import lombok.*;
import java.time.Instant;

/**
 * SKU Lottable definition entity.
 * Defines which lottable attributes are tracked for a SKU.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SKULottable {

    private LottableKey lottableKey;
    private SkuKey skuKey;

    // Lottable Field (01-10)
    private String lottableField; // LOTTABLE01, LOTTABLE02, etc.
    private int lottableIndex; // 1-10

    // Configuration
    private String label; // Display label for the field
    private String dataType; // STRING, DATE, NUMBER
    private boolean required;
    private boolean captureOnReceiving;
    private boolean displayOnShipping;
    private boolean trackInInventory;

    // Validation
    private String validationRule;
    private String defaultValue;
    private String formatMask;

    // Allocation Rules
    private boolean useInAllocation;
    private String allocationPriority; // FIFO, FEFO, etc.

    // Status
    private boolean active;

    // Audit
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;

    // Business Methods
    public boolean isDateField() {
        return "DATE".equalsIgnoreCase(dataType);
    }

    public boolean isExpiryField() {
        return label != null && (
                label.toUpperCase().contains("EXPIRY") ||
                label.toUpperCase().contains("EXPIRATION") ||
                label.toUpperCase().contains("BEST_BEFORE")
        );
    }

    public boolean isBatchField() {
        return label != null && (
                label.toUpperCase().contains("BATCH") ||
                label.toUpperCase().contains("LOT")
        );
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
