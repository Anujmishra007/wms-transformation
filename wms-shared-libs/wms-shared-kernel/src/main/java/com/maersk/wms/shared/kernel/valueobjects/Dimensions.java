package com.maersk.wms.shared.kernel.valueobjects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Value object representing physical dimensions (length, width, height).
 * Used across all WMS microservices for space calculations.
 *
 * @param length The length dimension
 * @param width The width dimension
 * @param height The height dimension
 * @param unit The dimension unit (CM, IN, M, FT)
 */
public record Dimensions(
        BigDecimal length,
        BigDecimal width,
        BigDecimal height,
        DimensionUnit unit
) implements Serializable {

    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    public Dimensions {
        if (length == null || width == null || height == null) {
            throw new IllegalArgumentException("Dimensions cannot be null");
        }
        if (unit == null) {
            unit = DimensionUnit.CM;
        }
        length = length.setScale(SCALE, ROUNDING);
        width = width.setScale(SCALE, ROUNDING);
        height = height.setScale(SCALE, ROUNDING);
    }

    public enum DimensionUnit {
        CM(1.0),
        IN(2.54),
        M(100.0),
        FT(30.48);

        private final double toCmFactor;

        DimensionUnit(double toCmFactor) {
            this.toCmFactor = toCmFactor;
        }

        public double toCmFactor() {
            return toCmFactor;
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // FACTORY METHODS
    // ═══════════════════════════════════════════════════════════════

    public static Dimensions of(double length, double width, double height, DimensionUnit unit) {
        return new Dimensions(
                BigDecimal.valueOf(length),
                BigDecimal.valueOf(width),
                BigDecimal.valueOf(height),
                unit
        );
    }

    public static Dimensions cm(double length, double width, double height) {
        return of(length, width, height, DimensionUnit.CM);
    }

    public static Dimensions inches(double length, double width, double height) {
        return of(length, width, height, DimensionUnit.IN);
    }

    // ═══════════════════════════════════════════════════════════════
    // CALCULATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Calculate volume (L x W x H).
     */
    public BigDecimal volume() {
        return length.multiply(width).multiply(height);
    }

    /**
     * Calculate dimensional weight for shipping.
     * Common formula: L x W x H / 5000 (for cm) = kg
     */
    public BigDecimal dimensionalWeight() {
        Dimensions inCm = convertTo(DimensionUnit.CM);
        return inCm.volume().divide(BigDecimal.valueOf(5000), SCALE, ROUNDING);
    }

    /**
     * Convert to different unit.
     */
    public Dimensions convertTo(DimensionUnit targetUnit) {
        if (this.unit == targetUnit) {
            return this;
        }
        double factor = this.unit.toCmFactor() / targetUnit.toCmFactor();
        return new Dimensions(
                this.length.multiply(BigDecimal.valueOf(factor)),
                this.width.multiply(BigDecimal.valueOf(factor)),
                this.height.multiply(BigDecimal.valueOf(factor)),
                targetUnit
        );
    }

    @Override
    public String toString() {
        return length.stripTrailingZeros().toPlainString() + " x " +
                width.stripTrailingZeros().toPlainString() + " x " +
                height.stripTrailingZeros().toPlainString() + " " + unit;
    }
}
