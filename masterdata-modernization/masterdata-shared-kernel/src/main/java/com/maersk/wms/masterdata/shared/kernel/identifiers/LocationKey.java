package com.maersk.wms.masterdata.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Location.
 */
public record LocationKey(String value) {
    public LocationKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("LocationKey cannot be null or blank");
        }
    }
}
