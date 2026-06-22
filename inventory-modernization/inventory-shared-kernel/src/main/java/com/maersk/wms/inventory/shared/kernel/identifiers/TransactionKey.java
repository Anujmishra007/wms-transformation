package com.maersk.wms.inventory.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Inventory Transactions.
 * Represents a unique inventory transaction/movement.
 */
public record TransactionKey(String value) {
    public TransactionKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TransactionKey cannot be null or blank");
        }
    }
}
