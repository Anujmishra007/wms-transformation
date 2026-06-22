package com.maersk.wms.inbound.shared.kernel.valueobjects;

import com.maersk.wms.inbound.shared.kernel.identifiers.SkuKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;

import java.util.Objects;

/**
 * Value object representing a line item in an inbound document.
 * Used across subdomains for consistent line item handling.
 *
 * Part of Shared Kernel - can be used by all bounded contexts.
 */
public final class InboundLineItem {

    private final StorerKey storerKey;
    private final SkuKey skuKey;
    private final Quantity expectedQty;
    private final Quantity receivedQty;
    private final LotAttributes lotAttributes;
    private final String packKey;
    private final String uom;

    private InboundLineItem(Builder builder) {
        this.storerKey = Objects.requireNonNull(builder.storerKey, "StorerKey is required");
        this.skuKey = Objects.requireNonNull(builder.skuKey, "SkuKey is required");
        this.expectedQty = builder.expectedQty;
        this.receivedQty = builder.receivedQty;
        this.lotAttributes = builder.lotAttributes;
        this.packKey = builder.packKey;
        this.uom = builder.uom;
    }

    public static Builder builder() {
        return new Builder();
    }

    public StorerKey getStorerKey() {
        return storerKey;
    }

    public SkuKey getSkuKey() {
        return skuKey;
    }

    public Quantity getExpectedQty() {
        return expectedQty;
    }

    public Quantity getReceivedQty() {
        return receivedQty;
    }

    public LotAttributes getLotAttributes() {
        return lotAttributes;
    }

    public String getPackKey() {
        return packKey;
    }

    public String getUom() {
        return uom;
    }

    public Quantity getOpenQty() {
        if (expectedQty == null) {
            return Quantity.zero(uom != null ? uom : "EA");
        }
        if (receivedQty == null) {
            return expectedQty;
        }
        return expectedQty.subtract(receivedQty);
    }

    public boolean isFullyReceived() {
        return getOpenQty().isZero() || getOpenQty().isNegative();
    }

    public boolean isOverReceived() {
        return getOpenQty().isNegative();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InboundLineItem that = (InboundLineItem) o;
        return Objects.equals(storerKey, that.storerKey) &&
               Objects.equals(skuKey, that.skuKey) &&
               Objects.equals(expectedQty, that.expectedQty) &&
               Objects.equals(receivedQty, that.receivedQty) &&
               Objects.equals(lotAttributes, that.lotAttributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storerKey, skuKey, expectedQty, receivedQty, lotAttributes);
    }

    @Override
    public String toString() {
        return "InboundLineItem{skuKey=" + skuKey + ", expectedQty=" + expectedQty + ", receivedQty=" + receivedQty + "}";
    }

    public static class Builder {
        private StorerKey storerKey;
        private SkuKey skuKey;
        private Quantity expectedQty;
        private Quantity receivedQty;
        private LotAttributes lotAttributes;
        private String packKey;
        private String uom;

        public Builder storerKey(StorerKey storerKey) {
            this.storerKey = storerKey;
            return this;
        }

        public Builder skuKey(SkuKey skuKey) {
            this.skuKey = skuKey;
            return this;
        }

        public Builder expectedQty(Quantity expectedQty) {
            this.expectedQty = expectedQty;
            return this;
        }

        public Builder receivedQty(Quantity receivedQty) {
            this.receivedQty = receivedQty;
            return this;
        }

        public Builder lotAttributes(LotAttributes lotAttributes) {
            this.lotAttributes = lotAttributes;
            return this;
        }

        public Builder packKey(String packKey) {
            this.packKey = packKey;
            return this;
        }

        public Builder uom(String uom) {
            this.uom = uom;
            return this;
        }

        public InboundLineItem build() {
            return new InboundLineItem(this);
        }
    }
}
