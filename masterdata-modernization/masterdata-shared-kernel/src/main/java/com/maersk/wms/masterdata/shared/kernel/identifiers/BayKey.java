package com.maersk.wms.masterdata.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Bay.
 */
public record BayKey(String value) {
    public BayKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("BayKey cannot be null or blank");
        }
    }
}
