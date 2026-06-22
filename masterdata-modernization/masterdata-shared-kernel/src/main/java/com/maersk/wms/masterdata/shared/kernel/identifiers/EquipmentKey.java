package com.maersk.wms.masterdata.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Equipment (forklifts, RF devices, MHE).
 */
public record EquipmentKey(String value) {
    public EquipmentKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("EquipmentKey cannot be null or blank");
        }
    }
}
