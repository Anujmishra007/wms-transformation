package com.maersk.wms.task.shared.kernel.identifiers;

/**
 * Value object representing a unique SKU identifier.
 */
public record SkuKey(String value) {
    public SkuKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("SkuKey cannot be null or blank");
        }
    }
}
