package com.maersk.wms.shared.kernel.identifiers;

import java.io.Serializable;

/**
 * Strongly-typed identifier for Aisle (warehouse layout).
 *
 * @param value The aisle identifier value
 */
public record AisleKey(String value) implements Serializable {

    public AisleKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Aisle key cannot be null or blank");
        }
    }

    public static AisleKey of(String value) {
        return new AisleKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
