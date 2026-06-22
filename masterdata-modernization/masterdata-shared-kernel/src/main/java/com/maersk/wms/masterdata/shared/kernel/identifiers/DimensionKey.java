package com.maersk.wms.masterdata.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for SKU Dimension record.
 */
public record DimensionKey(String value) {
    public DimensionKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("DimensionKey cannot be null or blank");
        }
    }
}
