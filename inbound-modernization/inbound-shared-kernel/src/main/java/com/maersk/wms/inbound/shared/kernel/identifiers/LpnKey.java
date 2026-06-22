package com.maersk.wms.inbound.shared.kernel.identifiers;

import java.util.Objects;

/**
 * Strongly-typed identifier for LPN (License Plate Number).
 * Used across subdomains to reference containers/pallets without tight coupling.
 *
 * Part of Shared Kernel - can be used by all bounded contexts.
 */
public final class LpnKey {

    private final String value;

    public LpnKey(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("LpnKey cannot be null or blank");
        }
        this.value = value;
    }

    public static LpnKey of(String value) {
        return new LpnKey(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LpnKey that = (LpnKey) o;
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
