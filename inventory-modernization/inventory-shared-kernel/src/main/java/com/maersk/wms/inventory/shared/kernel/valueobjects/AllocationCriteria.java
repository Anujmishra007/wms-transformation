package com.maersk.wms.inventory.shared.kernel.valueobjects;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Value object representing criteria for inventory allocation (FIFO).
 * Supports 20+ allocation variants based on different attribute combinations.
 */
public record AllocationCriteria(
        SkuKey skuKey,
        StorerKey storerKey,
        Quantity requestedQuantity,
        FifoStrategy fifoStrategy,
        List<String> preferredLocations,
        List<String> excludedLocations,
        List<String> allowedStatuses,
        LottableAttributes requiredLottables,
        boolean[] lottableMatchFlags,
        LocalDate expiryDateThreshold,
        boolean excludeOnHold,
        boolean excludeAllocated,
        Integer maxLocationsToAllocate,
        String zonePreference
) {

    public enum FifoStrategy {
        FIFO_RECEIPT_DATE,      // Standard FIFO by receipt date
        FIFO_EXPIRY_DATE,       // FEFO (First Expiry First Out)
        FIFO_MANUFACTURING_DATE, // By manufacturing date
        FIFO_LOTTABLE01,        // By lottable attribute 1
        FIFO_LOTTABLE02,        // By lottable attribute 2
        LIFO_RECEIPT_DATE,      // Last In First Out
        LOCATION_PRIORITY,      // By location pick sequence
        QUANTITY_ASCENDING,     // Smallest quantity first (location emptying)
        QUANTITY_DESCENDING,    // Largest quantity first (fewer picks)
        CUSTOM                  // Client-specific strategy
    }

    public static AllocationCriteria standardFifo(SkuKey skuKey, StorerKey storerKey, Quantity quantity) {
        return new AllocationCriteria(
                skuKey,
                storerKey,
                quantity,
                FifoStrategy.FIFO_RECEIPT_DATE,
                List.of(),
                List.of(),
                List.of("AVAILABLE"),
                LottableAttributes.empty(),
                new boolean[10],
                null,
                true,
                false,
                null,
                null
        );
    }

    public static AllocationCriteria fefo(SkuKey skuKey, StorerKey storerKey, Quantity quantity, LocalDate expiryThreshold) {
        return new AllocationCriteria(
                skuKey,
                storerKey,
                quantity,
                FifoStrategy.FIFO_EXPIRY_DATE,
                List.of(),
                List.of(),
                List.of("AVAILABLE"),
                LottableAttributes.empty(),
                new boolean[10],
                expiryThreshold,
                true,
                false,
                null,
                null
        );
    }

    public AllocationCriteria withPreferredLocations(List<String> locations) {
        return new AllocationCriteria(skuKey, storerKey, requestedQuantity, fifoStrategy,
                locations, excludedLocations, allowedStatuses, requiredLottables,
                lottableMatchFlags, expiryDateThreshold, excludeOnHold, excludeAllocated,
                maxLocationsToAllocate, zonePreference);
    }

    public AllocationCriteria withLottableRequirements(LottableAttributes lottables, boolean[] matchFlags) {
        return new AllocationCriteria(skuKey, storerKey, requestedQuantity, fifoStrategy,
                preferredLocations, excludedLocations, allowedStatuses, lottables,
                matchFlags, expiryDateThreshold, excludeOnHold, excludeAllocated,
                maxLocationsToAllocate, zonePreference);
    }

    public AllocationCriteria withZonePreference(String zone) {
        return new AllocationCriteria(skuKey, storerKey, requestedQuantity, fifoStrategy,
                preferredLocations, excludedLocations, allowedStatuses, requiredLottables,
                lottableMatchFlags, expiryDateThreshold, excludeOnHold, excludeAllocated,
                maxLocationsToAllocate, zone);
    }
}
