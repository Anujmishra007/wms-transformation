package com.maersk.wms.masterdata.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Zone.
 */
public record ZoneKey(String value) {
    public ZoneKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ZoneKey cannot be null or blank");
        }
    }
}
