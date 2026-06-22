package com.maersk.wms.inventory.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Inventory Holds.
 * Represents a hold/block on inventory.
 */
public record HoldKey(String value) {
    public HoldKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("HoldKey cannot be null or blank");
        }
    }
}
