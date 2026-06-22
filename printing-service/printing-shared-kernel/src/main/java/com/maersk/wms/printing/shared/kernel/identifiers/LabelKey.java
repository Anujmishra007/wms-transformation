package com.maersk.wms.printing.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Label.
 */
public record LabelKey(String value) {
    public LabelKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("LabelKey cannot be null or blank");
        }
    }
}
