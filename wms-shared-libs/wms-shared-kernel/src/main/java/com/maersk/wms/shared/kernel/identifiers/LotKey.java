package com.maersk.wms.shared.kernel.identifiers;

import java.io.Serializable;

/**
 * Strongly-typed identifier for Lot.
 * Used for lot-controlled inventory tracking.
 *
 * @param value The lot identifier value
 */
public record LotKey(String value) implements Serializable {

    public LotKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Lot key cannot be null or blank");
        }
    }

    /**
     * Create from string value.
     */
    public static LotKey of(String value) {
        return new LotKey(value);
    }

    /**
     * Create empty lot key (for non-lot-controlled items).
     */
    public static LotKey empty() {
        return new LotKey("NOLOT");
    }

    @Override
    public String toString() {
        return value;
    }
}
