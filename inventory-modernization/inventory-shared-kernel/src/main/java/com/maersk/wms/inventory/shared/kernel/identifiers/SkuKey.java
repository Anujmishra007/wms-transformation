package com.maersk.wms.inventory.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for SKU (Stock Keeping Unit).
 * Represents a unique product/item in the catalog.
 */
public record SkuKey(String value) {
    public SkuKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("SkuKey cannot be null or blank");
        }
    }
}
