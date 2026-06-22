package com.maersk.wms.inventory.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Lot.
 * Represents a specific lot/batch of inventory.
 */
public record LotKey(String value) {
    public LotKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("LotKey cannot be null or blank");
        }
    }
}
