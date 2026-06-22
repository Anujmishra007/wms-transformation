package com.maersk.wms.shared.kernel.identifiers;

import java.io.Serializable;

/**
 * Strongly-typed identifier for Wave.
 * Used across outbound, picking, and task microservices.
 *
 * @param value The wave identifier value
 */
public record WaveKey(String value) implements Serializable {

    public WaveKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Wave key cannot be null or blank");
        }
    }

    /**
     * Create from string value.
     */
    public static WaveKey of(String value) {
        return new WaveKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
