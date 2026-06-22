package com.maersk.wms.inventory.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for License Plate Number (LPN/ID).
 * Represents a container/pallet/case in the warehouse.
 */
public record LpnKey(String value) {
    public LpnKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("LpnKey cannot be null or blank");
        }
    }
}
