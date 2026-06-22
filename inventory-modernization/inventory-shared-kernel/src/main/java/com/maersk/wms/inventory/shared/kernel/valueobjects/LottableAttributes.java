package com.maersk.wms.inventory.shared.kernel.valueobjects;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

/**
 * Value object representing the 10 configurable lottable attributes.
 * These attributes are used for lot tracking, FIFO allocation, and client-specific requirements.
 */
public record LottableAttributes(
        String lottable01,  // Often: Batch/Lot Number
        String lottable02,  // Often: Expiry Date
        String lottable03,  // Often: Manufacturing Date
        String lottable04,  // Often: Country of Origin
        String lottable05,  // Often: Vendor Lot
        String lottable06,  // Client configurable
        String lottable07,  // Client configurable
        String lottable08,  // Client configurable
        String lottable09,  // Client configurable
        String lottable10   // Client configurable
) {

    public static LottableAttributes empty() {
        return new LottableAttributes(null, null, null, null, null, null, null, null, null, null);
    }

    public static LottableAttributes of(String lottable01, String lottable02) {
        return new LottableAttributes(lottable01, lottable02, null, null, null, null, null, null, null, null);
    }

    public static LottableAttributes fromMap(Map<String, String> attributes) {
        return new LottableAttributes(
                attributes.getOrDefault("LOTTABLE01", null),
                attributes.getOrDefault("LOTTABLE02", null),
                attributes.getOrDefault("LOTTABLE03", null),
                attributes.getOrDefault("LOTTABLE04", null),
                attributes.getOrDefault("LOTTABLE05", null),
                attributes.getOrDefault("LOTTABLE06", null),
                attributes.getOrDefault("LOTTABLE07", null),
                attributes.getOrDefault("LOTTABLE08", null),
                attributes.getOrDefault("LOTTABLE09", null),
                attributes.getOrDefault("LOTTABLE10", null)
        );
    }

    public Map<String, String> toMap() {
        return Map.of(
                "LOTTABLE01", Objects.requireNonNullElse(lottable01, ""),
                "LOTTABLE02", Objects.requireNonNullElse(lottable02, ""),
                "LOTTABLE03", Objects.requireNonNullElse(lottable03, ""),
                "LOTTABLE04", Objects.requireNonNullElse(lottable04, ""),
                "LOTTABLE05", Objects.requireNonNullElse(lottable05, ""),
                "LOTTABLE06", Objects.requireNonNullElse(lottable06, ""),
                "LOTTABLE07", Objects.requireNonNullElse(lottable07, ""),
                "LOTTABLE08", Objects.requireNonNullElse(lottable08, ""),
                "LOTTABLE09", Objects.requireNonNullElse(lottable09, ""),
                "LOTTABLE10", Objects.requireNonNullElse(lottable10, "")
        );
    }

    public LottableAttributes withLottable01(String value) {
        return new LottableAttributes(value, lottable02, lottable03, lottable04, lottable05,
                lottable06, lottable07, lottable08, lottable09, lottable10);
    }

    public LottableAttributes withLottable02(String value) {
        return new LottableAttributes(lottable01, value, lottable03, lottable04, lottable05,
                lottable06, lottable07, lottable08, lottable09, lottable10);
    }

    /**
     * Parse expiry date from lottable02 (common usage pattern).
     */
    public LocalDate getExpiryDate() {
        if (lottable02 == null || lottable02.isBlank()) return null;
        try {
            return LocalDate.parse(lottable02);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Alias for getExpiryDate() for record-style access.
     */
    public LocalDate expiryDate() {
        return getExpiryDate();
    }

    /**
     * Parse manufacturing date from lottable03 (common usage pattern).
     */
    public LocalDate getManufacturingDate() {
        if (lottable03 == null || lottable03.isBlank()) return null;
        try {
            return LocalDate.parse(lottable03);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Check if inventory is expired based on lottable02.
     */
    public boolean isExpired() {
        LocalDate expiry = getExpiryDate();
        return expiry != null && expiry.isBefore(LocalDate.now());
    }

    /**
     * Check if lottables match for allocation purposes.
     */
    public boolean matchesForAllocation(LottableAttributes other, boolean[] matchFlags) {
        if (matchFlags.length < 10) return false;

        return (!matchFlags[0] || Objects.equals(lottable01, other.lottable01))
                && (!matchFlags[1] || Objects.equals(lottable02, other.lottable02))
                && (!matchFlags[2] || Objects.equals(lottable03, other.lottable03))
                && (!matchFlags[3] || Objects.equals(lottable04, other.lottable04))
                && (!matchFlags[4] || Objects.equals(lottable05, other.lottable05))
                && (!matchFlags[5] || Objects.equals(lottable06, other.lottable06))
                && (!matchFlags[6] || Objects.equals(lottable07, other.lottable07))
                && (!matchFlags[7] || Objects.equals(lottable08, other.lottable08))
                && (!matchFlags[8] || Objects.equals(lottable09, other.lottable09))
                && (!matchFlags[9] || Objects.equals(lottable10, other.lottable10));
    }
}
