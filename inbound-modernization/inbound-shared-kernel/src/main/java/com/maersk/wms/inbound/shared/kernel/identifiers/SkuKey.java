package com.maersk.wms.inbound.shared.kernel.identifiers;

import java.util.Objects;

/**
 * Strongly-typed identifier for SKU (Stock Keeping Unit).
 * Composite key of storer + sku code.
 *
 * Part of Shared Kernel - can be used by all bounded contexts.
 */
public final class SkuKey {

    private final String storerKey;
    private final String sku;

    public SkuKey(String storerKey, String sku) {
        if (storerKey == null || storerKey.isBlank()) {
            throw new IllegalArgumentException("StorerKey cannot be null or blank");
        }
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("SKU cannot be null or blank");
        }
        this.storerKey = storerKey;
        this.sku = sku;
    }

    public static SkuKey of(String storerKey, String sku) {
        return new SkuKey(storerKey, sku);
    }

    public static SkuKey of(StorerKey storerKey, String sku) {
        return new SkuKey(storerKey.getValue(), sku);
    }

    public String getStorerKey() {
        return storerKey;
    }

    public String getSku() {
        return sku;
    }

    public String getValue() {
        return storerKey + ":" + sku;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkuKey skuKey = (SkuKey) o;
        return Objects.equals(storerKey, skuKey.storerKey) && Objects.equals(sku, skuKey.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storerKey, sku);
    }

    @Override
    public String toString() {
        return storerKey + ":" + sku;
    }
}
