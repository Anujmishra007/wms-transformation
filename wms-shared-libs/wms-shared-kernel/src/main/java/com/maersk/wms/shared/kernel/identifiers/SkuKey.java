package com.maersk.wms.shared.kernel.identifiers;

import java.io.Serializable;

/**
 * Strongly-typed identifier for SKU (Stock Keeping Unit).
 * Used across all WMS microservices for product identification.
 *
 * @param value The SKU identifier value
 */
public record SkuKey(String value) implements Serializable {

    public SkuKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("SKU key cannot be null or blank");
        }
    }

    /**
     * Create from string value.
     */
    public static SkuKey of(String value) {
        return new SkuKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
