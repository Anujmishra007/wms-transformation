package com.maersk.wms.printing.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Zone (reference from Master Data).
 */
public record ZoneKey(String value) {
    public ZoneKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ZoneKey cannot be null or blank");
        }
    }
}
