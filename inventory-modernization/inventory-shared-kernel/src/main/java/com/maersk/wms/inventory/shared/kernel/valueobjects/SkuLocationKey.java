package com.maersk.wms.inventory.shared.kernel.valueobjects;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;

/**
 * Value object representing the alternative inventory key: SKUxLOC.
 * Used for SKU-level inventory queries and aggregations.
 *
 * Represents: SKU × Location
 */
public record SkuLocationKey(
        SkuKey skuKey,
        LocationKey locationKey
) {

    public SkuLocationKey {
        if (skuKey == null) {
            throw new IllegalArgumentException("SkuKey cannot be null");
        }
        if (locationKey == null) {
            throw new IllegalArgumentException("LocationKey cannot be null");
        }
    }

    public static SkuLocationKey of(String sku, String location) {
        return new SkuLocationKey(
                new SkuKey(sku),
                new LocationKey(location)
        );
    }

    /**
     * Generate the key string: SKU|LOCATION
     */
    public String toKeyString() {
        return String.format("%s|%s", skuKey.value(), locationKey.value());
    }

    /**
     * Parse key from string format.
     */
    public static SkuLocationKey fromKeyString(String keyString) {
        String[] parts = keyString.split("\\|");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid SKUxLOC key format: " + keyString);
        }
        return new SkuLocationKey(
                new SkuKey(parts[0]),
                new LocationKey(parts[1])
        );
    }
}
