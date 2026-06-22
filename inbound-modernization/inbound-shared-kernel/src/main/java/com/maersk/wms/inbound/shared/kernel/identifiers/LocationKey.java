package com.maersk.wms.inbound.shared.kernel.identifiers;

import java.util.Objects;

/**
 * Strongly-typed identifier for Location.
 * Used across subdomains to reference warehouse locations without tight coupling.
 *
 * Part of Shared Kernel - can be used by all bounded contexts.
 */
public final class LocationKey {

    private final String value;

    public LocationKey(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("LocationKey cannot be null or blank");
        }
        this.value = value;
    }

    public static LocationKey of(String value) {
        return new LocationKey(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationKey that = (LocationKey) o;
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
