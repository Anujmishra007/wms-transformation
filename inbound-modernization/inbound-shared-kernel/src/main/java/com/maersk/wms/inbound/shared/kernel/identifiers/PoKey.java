package com.maersk.wms.inbound.shared.kernel.identifiers;

import java.util.Objects;

/**
 * Strongly-typed identifier for Purchase Order.
 * Used across subdomains to reference POs without tight coupling.
 *
 * Part of Shared Kernel - can be used by all bounded contexts.
 */
public final class PoKey {

    private final String value;

    public PoKey(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PoKey cannot be null or blank");
        }
        this.value = value;
    }

    public static PoKey of(String value) {
        return new PoKey(value);
    }

    public String getValue() {
        return value;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PoKey that = (PoKey) o;
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
