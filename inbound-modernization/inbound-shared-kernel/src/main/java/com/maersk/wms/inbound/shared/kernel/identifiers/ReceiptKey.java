package com.maersk.wms.inbound.shared.kernel.identifiers;

import java.util.Objects;

/**
 * Strongly-typed identifier for Receipt aggregate.
 * Used across subdomains to reference receipts without tight coupling.
 *
 * Part of Shared Kernel - can be used by all bounded contexts.
 */
public final class ReceiptKey {

    private final String value;

    public ReceiptKey(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ReceiptKey cannot be null or blank");
        }
        this.value = value;
    }

    public static ReceiptKey of(String value) {
        return new ReceiptKey(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReceiptKey that = (ReceiptKey) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
