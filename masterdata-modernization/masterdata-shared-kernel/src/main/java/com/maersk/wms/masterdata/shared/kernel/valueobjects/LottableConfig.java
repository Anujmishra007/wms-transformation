package com.maersk.wms.masterdata.shared.kernel.valueobjects;

import java.util.List;
import java.util.Map;

/**
 * Value object representing lottable attribute configuration.
 * Lottables are tracking attributes like expiry date, batch number, etc.
 */
public record LottableConfig(
        boolean trackExpiryDate,
        boolean trackManufactureDate,
        boolean trackBatchNumber,
        boolean trackLotNumber,
        boolean trackSerialNumber,
        int shelfLifeDays,
        int inboundShelfLifeDays,
        int outboundShelfLifeDays,
        List<String> customLottables,
        Map<String, String> lottableLabels
) {
    public LottableConfig {
        if (shelfLifeDays < 0) {
            throw new IllegalArgumentException("Shelf life days cannot be negative");
        }
    }

    public static LottableConfig none() {
        return new LottableConfig(
                false, false, false, false, false,
                0, 0, 0, List.of(), Map.of()
        );
    }

    public static LottableConfig withExpiry(int shelfLifeDays) {
        return new LottableConfig(
                true, false, false, false, false,
                shelfLifeDays, shelfLifeDays, 0, List.of(), Map.of()
        );
    }

    public static LottableConfig withBatchAndExpiry(int shelfLifeDays) {
        return new LottableConfig(
                true, false, true, true, false,
                shelfLifeDays, shelfLifeDays, 0, List.of(), Map.of()
        );
    }

    public boolean hasAnyTracking() {
        return trackExpiryDate || trackManufactureDate || trackBatchNumber ||
               trackLotNumber || trackSerialNumber || !customLottables.isEmpty();
    }
}
