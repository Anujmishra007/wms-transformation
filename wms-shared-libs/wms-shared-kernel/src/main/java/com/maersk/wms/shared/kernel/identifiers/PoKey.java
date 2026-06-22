package com.maersk.wms.shared.kernel.identifiers;

import java.io.Serializable;

/**
 * Strongly-typed identifier for Purchase Order.
 * Used across inbound and receiving microservices.
 *
 * @param value The PO identifier value
 */
public record PoKey(String value) implements Serializable {

    public PoKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PO key cannot be null or blank");
        }
    }

    /**
     * Create from string value.
     */
    public static PoKey of(String value) {
        return new PoKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
