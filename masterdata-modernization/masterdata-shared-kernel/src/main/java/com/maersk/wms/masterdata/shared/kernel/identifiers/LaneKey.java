package com.maersk.wms.masterdata.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Lane (staging/processing lane).
 */
public record LaneKey(String value) {
    public LaneKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("LaneKey cannot be null or blank");
        }
    }
}
