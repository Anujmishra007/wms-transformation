package com.maersk.wms.shared.kernel.identifiers;

import java.io.Serializable;

/**
 * Strongly-typed identifier for Location.
 * Used across all WMS microservices for warehouse location identification.
 *
 * @param value The location identifier value
 */
public record LocationKey(String value) implements Serializable {

    public LocationKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Location key cannot be null or blank");
        }
    }

    /**
     * Create from string value.
     */
    public static LocationKey of(String value) {
        return new LocationKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
