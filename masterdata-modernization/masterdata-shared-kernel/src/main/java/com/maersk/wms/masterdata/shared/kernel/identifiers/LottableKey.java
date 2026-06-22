package com.maersk.wms.masterdata.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Lottable configuration.
 */
public record LottableKey(String value) {
    public LottableKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("LottableKey cannot be null or blank");
        }
    }
}
