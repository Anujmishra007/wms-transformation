package com.maersk.wms.shared.kernel.identifiers;

import java.io.Serializable;

/**
 * Strongly-typed identifier for Transaction (audit trail).
 * Used for inventory transaction history.
 *
 * @param value The transaction identifier value
 */
public record TransactionKey(String value) implements Serializable {

    public TransactionKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Transaction key cannot be null or blank");
        }
    }

    /**
     * Create from string value.
     */
    public static TransactionKey of(String value) {
        return new TransactionKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
