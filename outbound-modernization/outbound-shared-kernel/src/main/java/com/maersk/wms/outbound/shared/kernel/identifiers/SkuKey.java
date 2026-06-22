package com.maersk.wms.outbound.shared.kernel.identifiers;

import lombok.Value;

/**
 * Unique identifier for a SKU (Storer + SKU).
 */
@Value
public class SkuKey {
    String storerKey;
    String sku;

    public static SkuKey of(String storerKey, String sku) {
        return new SkuKey(storerKey, sku);
    }
}
