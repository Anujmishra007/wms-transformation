package com.maersk.wms.shared.kernel.identifiers;

import java.io.Serializable;

/**
 * Strongly-typed identifier for Pick (pick detail).
 * Used in picking operations.
 *
 * @param value The pick identifier value
 */
public record PickKey(String value) implements Serializable {

    public PickKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Pick key cannot be null or blank");
        }
    }

    /**
     * Create from string value.
     */
    public static PickKey of(String value) {
        return new PickKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
