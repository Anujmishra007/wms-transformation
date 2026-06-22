package com.maersk.wms.inbound.shared.kernel.identifiers;

import java.util.Objects;

/**
 * Strongly-typed identifier for ASN (Advance Shipping Notice).
 * Used across subdomains to reference ASNs without tight coupling.
 *
 * Part of Shared Kernel - can be used by all bounded contexts.
 */
public final class AsnKey {

    private final String value;

    public AsnKey(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AsnKey cannot be null or blank");
        }
        this.value = value;
    }

    public static AsnKey of(String value) {
        return new AsnKey(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AsnKey that = (AsnKey) o;
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
