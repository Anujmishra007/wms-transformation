package com.maersk.wms.shared.kernel.identifiers;

import java.io.Serializable;

/**
 * Strongly-typed identifier for Zone.
 * Used across task, masterdata, and printing microservices.
 *
 * @param value The zone identifier value
 */
public record ZoneKey(String value) implements Serializable {

    public ZoneKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Zone key cannot be null or blank");
        }
    }

    /**
     * Create from string value.
     */
    public static ZoneKey of(String value) {
        return new ZoneKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
