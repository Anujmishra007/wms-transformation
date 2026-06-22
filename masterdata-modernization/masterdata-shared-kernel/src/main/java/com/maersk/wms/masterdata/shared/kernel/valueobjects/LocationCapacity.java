package com.maersk.wms.masterdata.shared.kernel.valueobjects;

import java.math.BigDecimal;

/**
 * Value object representing location storage capacity.
 */
public record LocationCapacity(
        BigDecimal maxWeight,
        BigDecimal maxVolume,
        int maxPallets,
        int maxCases,
        int maxUnits,
        String weightUom,
        String volumeUom
) {
    public LocationCapacity {
        if (maxWeight != null && maxWeight.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Max weight cannot be negative");
        }
        if (maxVolume != null && maxVolume.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Max volume cannot be negative");
        }
        if (maxPallets < 0) {
            throw new IllegalArgumentException("Max pallets cannot be negative");
        }
    }

    public static LocationCapacity unlimited() {
        return new LocationCapacity(
                BigDecimal.valueOf(999999), BigDecimal.valueOf(999999),
                9999, 9999, 999999, "KG", "M3"
        );
    }

    public static LocationCapacity forPallets(int maxPallets, BigDecimal maxWeight) {
        return new LocationCapacity(maxWeight, null, maxPallets, 0, 0, "KG", "M3");
    }

    public boolean canFitWeight(BigDecimal weight) {
        return maxWeight == null || maxWeight.compareTo(weight) >= 0;
    }

    public boolean canFitVolume(BigDecimal volume) {
        return maxVolume == null || maxVolume.compareTo(volume) >= 0;
    }
}
