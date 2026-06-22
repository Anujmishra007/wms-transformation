package com.maersk.wms.shared.kernel.valueobjects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Value object representing a quantity with unit of measure.
 * Immutable and provides arithmetic operations.
 * Used across all WMS microservices.
 *
 * @param value The numeric quantity value
 * @param uom Unit of measure (EA, CS, PL, etc.)
 */
public record Quantity(BigDecimal value, String uom) implements Serializable, Comparable<Quantity> {

    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    public Quantity {
        if (value == null) {
            throw new IllegalArgumentException("Quantity value cannot be null");
        }
        if (uom == null || uom.isBlank()) {
            uom = "EA"; // Default UOM
        }
        value = value.setScale(SCALE, ROUNDING);
    }

    // ═══════════════════════════════════════════════════════════════
    // FACTORY METHODS
    // ═══════════════════════════════════════════════════════════════

    public static Quantity of(double value, String uom) {
        return new Quantity(BigDecimal.valueOf(value), uom);
    }

    public static Quantity of(int value, String uom) {
        return new Quantity(BigDecimal.valueOf(value), uom);
    }

    public static Quantity of(BigDecimal value, String uom) {
        return new Quantity(value, uom);
    }

    public static Quantity zero(String uom) {
        return new Quantity(BigDecimal.ZERO, uom);
    }

    public static Quantity eaches(double value) {
        return of(value, "EA");
    }

    public static Quantity cases(double value) {
        return of(value, "CS");
    }

    public static Quantity pallets(double value) {
        return of(value, "PL");
    }

    // ═══════════════════════════════════════════════════════════════
    // ARITHMETIC OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    public Quantity add(Quantity other) {
        validateSameUom(other);
        return new Quantity(this.value.add(other.value), this.uom);
    }

    public Quantity subtract(Quantity other) {
        validateSameUom(other);
        return new Quantity(this.value.subtract(other.value), this.uom);
    }

    public Quantity multiply(double factor) {
        return new Quantity(this.value.multiply(BigDecimal.valueOf(factor)), this.uom);
    }

    public Quantity multiply(BigDecimal factor) {
        return new Quantity(this.value.multiply(factor), this.uom);
    }

    public Quantity divide(double divisor) {
        return new Quantity(this.value.divide(BigDecimal.valueOf(divisor), SCALE, ROUNDING), this.uom);
    }

    public Quantity negate() {
        return new Quantity(this.value.negate(), this.uom);
    }

    public Quantity abs() {
        return new Quantity(this.value.abs(), this.uom);
    }

    public Quantity min(Quantity other) {
        validateSameUom(other);
        return this.value.compareTo(other.value) <= 0 ? this : other;
    }

    public Quantity max(Quantity other) {
        validateSameUom(other);
        return this.value.compareTo(other.value) >= 0 ? this : other;
    }

    // ═══════════════════════════════════════════════════════════════
    // COMPARISON OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    public boolean isZero() {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositive() {
        return value.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegative() {
        return value.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isZeroOrNegative() {
        return value.compareTo(BigDecimal.ZERO) <= 0;
    }

    public boolean isGreaterThan(Quantity other) {
        validateSameUom(other);
        return this.value.compareTo(other.value) > 0;
    }

    public boolean isGreaterThanOrEqual(Quantity other) {
        validateSameUom(other);
        return this.value.compareTo(other.value) >= 0;
    }

    public boolean isLessThan(Quantity other) {
        validateSameUom(other);
        return this.value.compareTo(other.value) < 0;
    }

    public boolean isLessThanOrEqual(Quantity other) {
        validateSameUom(other);
        return this.value.compareTo(other.value) <= 0;
    }

    @Override
    public int compareTo(Quantity other) {
        validateSameUom(other);
        return this.value.compareTo(other.value);
    }

    // ═══════════════════════════════════════════════════════════════
    // CONVERSION
    // ═══════════════════════════════════════════════════════════════

    public double doubleValue() {
        return value.doubleValue();
    }

    public int intValue() {
        return value.intValue();
    }

    public long longValue() {
        return value.longValue();
    }

    public Quantity withUom(String newUom) {
        return new Quantity(this.value, newUom);
    }

    // ═══════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ═══════════════════════════════════════════════════════════════

    private void validateSameUom(Quantity other) {
        if (!this.uom.equalsIgnoreCase(other.uom)) {
            throw new IllegalArgumentException(
                    "Cannot operate on quantities with different UOMs: " + this.uom + " vs " + other.uom);
        }
    }

    @Override
    public String toString() {
        return value.stripTrailingZeros().toPlainString() + " " + uom;
    }
}
