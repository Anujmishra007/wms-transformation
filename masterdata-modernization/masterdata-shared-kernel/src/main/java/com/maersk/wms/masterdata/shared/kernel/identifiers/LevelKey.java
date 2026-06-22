package com.maersk.wms.masterdata.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Level (rack level).
 */
public record LevelKey(String value) {
    public LevelKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("LevelKey cannot be null or blank");
        }
    }
}
