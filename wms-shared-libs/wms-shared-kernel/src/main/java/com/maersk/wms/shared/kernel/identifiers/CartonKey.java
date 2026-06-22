package com.maersk.wms.shared.kernel.identifiers;

import java.io.Serializable;

/**
 * Strongly-typed identifier for Carton.
 * Used in packing and shipping operations.
 *
 * @param value The carton identifier value
 */
public record CartonKey(String value) implements Serializable {

    public CartonKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Carton key cannot be null or blank");
        }
    }

    /**
     * Create from string value.
     */
    public static CartonKey of(String value) {
        return new CartonKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
