package com.maersk.wms.inbound.shared.kernel.identifiers;

import java.util.Objects;

/**
 * Strongly-typed identifier for Storer (customer/owner).
 * Used across subdomains to reference storers without tight coupling.
 *
 * Part of Shared Kernel - can be used by all bounded contexts.
 */
public final class StorerKey {

    private final String value;

    public StorerKey(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("StorerKey cannot be null or blank");
        }
        this.value = value;
    }

    public static StorerKey of(String value) {
        return new StorerKey(value);
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
        StorerKey that = (StorerKey) o;
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
