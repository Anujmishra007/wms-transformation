package com.maersk.wms.inventory.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Inventory Transfers.
 * Represents a movement between locations.
 */
public record TransferKey(String value) {
    public TransferKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TransferKey cannot be null or blank");
        }
    }
}
