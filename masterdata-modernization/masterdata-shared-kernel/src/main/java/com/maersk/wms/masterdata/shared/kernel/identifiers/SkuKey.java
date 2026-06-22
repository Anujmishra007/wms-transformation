package com.maersk.wms.masterdata.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for SKU (Stock Keeping Unit).
 */
public record SkuKey(String value) {
    public SkuKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("SkuKey cannot be null or blank");
        }
    }
}
