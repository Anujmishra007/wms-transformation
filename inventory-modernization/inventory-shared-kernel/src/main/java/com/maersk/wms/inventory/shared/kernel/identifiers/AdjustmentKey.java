package com.maersk.wms.inventory.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Inventory Adjustments.
 * Represents a quantity or status adjustment.
 */
public record AdjustmentKey(String value) {
    public AdjustmentKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AdjustmentKey cannot be null or blank");
        }
    }
}
