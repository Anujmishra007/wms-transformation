package com.maersk.wms.shared.kernel.identifiers;

import java.io.Serializable;

/**
 * Strongly-typed identifier for Hold (inventory holds).
 *
 * @param value The hold identifier value
 */
public record HoldKey(String value) implements Serializable {

    public HoldKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Hold key cannot be null or blank");
        }
    }

    public static HoldKey of(String value) {
        return new HoldKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
