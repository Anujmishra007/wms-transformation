package com.maersk.wms.shared.kernel.identifiers;

import java.io.Serializable;

/**
 * Strongly-typed identifier for Dock (receiving/shipping docks).
 *
 * @param value The dock identifier value
 */
public record DockKey(String value) implements Serializable {

    public DockKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Dock key cannot be null or blank");
        }
    }

    public static DockKey of(String value) {
        return new DockKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
