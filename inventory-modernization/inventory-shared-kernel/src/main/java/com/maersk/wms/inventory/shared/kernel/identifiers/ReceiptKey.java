package com.maersk.wms.inventory.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Receipt.
 * Represents an inbound receipt that creates inventory.
 */
public record ReceiptKey(String value) {
    public ReceiptKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ReceiptKey cannot be null or blank");
        }
    }
}
