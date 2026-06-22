package com.maersk.wms.shared.kernel.identifiers;

import java.io.Serializable;

/**
 * Strongly-typed identifier for ASN (Advanced Shipping Notice).
 * Used in inbound operations.
 *
 * @param value The ASN identifier value
 */
public record AsnKey(String value) implements Serializable {

    public AsnKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ASN key cannot be null or blank");
        }
    }

    /**
     * Create from string value.
     */
    public static AsnKey of(String value) {
        return new AsnKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
