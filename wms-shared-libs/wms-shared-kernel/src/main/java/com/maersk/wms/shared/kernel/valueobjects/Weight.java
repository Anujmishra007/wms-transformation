package com.maersk.wms.shared.kernel.valueobjects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Value object representing weight with unit of measure.
 * Used across all WMS microservices for weight calculations.
 *
 * @param value The weight value
 * @param unit The weight unit (KG, LB, OZ, G)
 */
public record Weight(BigDecimal value, WeightUnit unit) implements Serializable, Comparable<Weight> {

    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    public Weight {
        if (value == null) {
            throw new IllegalArgumentException("Weight value cannot be null");
        }
        if (unit == null) {
            unit = WeightUnit.KG;
        }
        value = value.setScale(SCALE, ROUNDING);
    }

    public enum WeightUnit {
        KG(1.0),
        LB(0.453592),
        OZ(0.0283495),
        G(0.001);

        private final double toKgFactor;

        WeightUnit(double toKgFactor) {
            this.toKgFactor = toKgFactor;
        }

        public double toKgFactor() {
            return toKgFactor;
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // FACTORY METHODS
    // ═══════════════════════════════════════════════════════════════

    public static Weight of(double value, WeightUnit unit) {
        return new Weight(BigDecimal.valueOf(value), unit);
    }

    public static Weight kg(double value) {
        return of(value, WeightUnit.KG);
    }

    public static Weight lb(double value) {
        return of(value, WeightUnit.LB);
    }

    public static Weight zero(WeightUnit unit) {
        return new Weight(BigDecimal.ZERO, unit);
    }

    // ═══════════════════════════════════════════════════════════════
    // ARITHMETIC OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    public Weight add(Weight other) {
        Weight otherConverted = other.convertTo(this.unit);
        return new Weight(this.value.add(otherConverted.value), this.unit);
    }

    public Weight subtract(Weight other) {
        Weight otherConverted = other.convertTo(this.unit);
        return new Weight(this.value.subtract(otherConverted.value), this.unit);
    }

    public Weight multiply(double factor) {
        return new Weight(this.value.multiply(BigDecimal.valueOf(factor)), this.unit);
    }

    // ═══════════════════════════════════════════════════════════════
    // CONVERSION
    // ═══════════════════════════════════════════════════════════════

    public Weight convertTo(WeightUnit targetUnit) {
        if (this.unit == targetUnit) {
            return this;
        }
        // Convert to KG first, then to target unit
        double inKg = this.value.doubleValue() * this.unit.toKgFactor();
        double inTargetUnit = inKg / targetUnit.toKgFactor();
        return new Weight(BigDecimal.valueOf(inTargetUnit), targetUnit);
    }

    public Weight toKg() {
        return convertTo(WeightUnit.KG);
    }

    public Weight toLb() {
        return convertTo(WeightUnit.LB);
    }

    // ═══════════════════════════════════════════════════════════════
    // COMPARISON
    // ═══════════════════════════════════════════════════════════════

    public boolean isZero() {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    @Override
    public int compareTo(Weight other) {
        Weight otherInSameUnit = other.convertTo(this.unit);
        return this.value.compareTo(otherInSameUnit.value);
    }

    @Override
    public String toString() {
        return value.stripTrailingZeros().toPlainString() + " " + unit;
    }
}
