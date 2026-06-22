package com.maersk.wms.shared.kernel.identifiers;

import java.io.Serializable;

/**
 * Strongly-typed identifier for Receipt.
 * Used across inbound and inventory microservices.
 *
 * @param value The receipt identifier value
 */
public record ReceiptKey(String value) implements Serializable {

    public ReceiptKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Receipt key cannot be null or blank");
        }
    }

    /**
     * Create from string value.
     */
    public static ReceiptKey of(String value) {
        return new ReceiptKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
