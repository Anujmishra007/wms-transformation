package com.maersk.wms.shared.kernel.identifiers;

import java.io.Serializable;

/**
 * Strongly-typed identifier for Equipment (forklifts, pallet jacks, etc.).
 *
 * @param value The equipment identifier value
 */
public record EquipmentKey(String value) implements Serializable {

    public EquipmentKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Equipment key cannot be null or blank");
        }
    }

    public static EquipmentKey of(String value) {
        return new EquipmentKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
