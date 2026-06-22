package com.maersk.wms.inventory.shared.kernel.valueobjects;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;

/**
 * Value object representing the composite key for inventory: LOTxLOCxID.
 * This is the primary uniqueness constraint in the inventory system.
 *
 * Represents: Lot × Location × License Plate (ID)
 */
public record InventoryCompositeKey(
        LotKey lotKey,
        LocationKey locationKey,
        LpnKey lpnKey
) {

    public InventoryCompositeKey {
        if (lotKey == null) {
            throw new IllegalArgumentException("LotKey cannot be null");
        }
        if (locationKey == null) {
            throw new IllegalArgumentException("LocationKey cannot be null");
        }
        if (lpnKey == null) {
            throw new IllegalArgumentException("LpnKey cannot be null");
        }
    }

    public static InventoryCompositeKey of(String lot, String location, String lpn) {
        return new InventoryCompositeKey(
                new LotKey(lot),
                new LocationKey(location),
                new LpnKey(lpn)
        );
    }

    /**
     * Generate the composite key string: LOT|LOCATION|ID
     */
    public String toKeyString() {
        return String.format("%s|%s|%s",
                lotKey.value(),
                locationKey.value(),
                lpnKey.value());
    }

    /**
     * Parse composite key from string format.
     */
    public static InventoryCompositeKey fromKeyString(String keyString) {
        String[] parts = keyString.split("\\|");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid composite key format: " + keyString);
        }
        return new InventoryCompositeKey(
                new LotKey(parts[0]),
                new LocationKey(parts[1]),
                new LpnKey(parts[2])
        );
    }
}
