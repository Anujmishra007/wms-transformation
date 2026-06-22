package com.maersk.wms.inventory.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Inventory Allocations.
 * Represents a reservation of inventory for an order.
 */
public record AllocationKey(String value) {
    public AllocationKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AllocationKey cannot be null or blank");
        }
    }
}
