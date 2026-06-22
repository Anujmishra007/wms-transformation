package com.maersk.wms.masterdata.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Aisle.
 */
public record AisleKey(String value) {
    public AisleKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AisleKey cannot be null or blank");
        }
    }
}
