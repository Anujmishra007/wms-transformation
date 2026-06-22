package com.maersk.wms.shared.kernel.identifiers;

import java.io.Serializable;

/**
 * Strongly-typed identifier for Storer (Customer/Owner).
 * Used across all WMS microservices for inventory ownership identification.
 *
 * @param value The storer identifier value
 */
public record StorerKey(String value) implements Serializable {

    public StorerKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Storer key cannot be null or blank");
        }
    }

    /**
     * Create from string value.
     */
    public static StorerKey of(String value) {
        return new StorerKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
