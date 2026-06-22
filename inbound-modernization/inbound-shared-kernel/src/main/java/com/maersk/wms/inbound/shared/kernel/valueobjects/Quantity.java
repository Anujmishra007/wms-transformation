package com.maersk.wms.inbound.shared.kernel.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value object representing a quantity with unit of measure.
 * Immutable and used across all inbound subdomains for consistent quantity handling.
 *
 * Part of Shared Kernel - can be used by all bounded contexts.
 */
public final class Quantity {

    private final BigDecimal value;
    private final String uom;

    public Quantity(BigDecimal value, String uom) {
        if (value == null) {
            throw new IllegalArgumentException("Quantity value cannot be null");
        }
        if (uom == null || uom.isBlank()) {
            throw new IllegalArgumentException("Unit of measure cannot be null or blank");
        }
        this.value = value;
        this.uom = uom;
    }

    public static Quantity of(BigDecimal value, String uom) {
        return new Quantity(value, uom);
    }

    public static Quantity of(int value, String uom) {
        return new Quantity(BigDecimal.valueOf(value), uom);
    }

    public static Quantity of(double value, String uom) {
        return new Quantity(BigDecimal.valueOf(value), uom);
    }

    public static Quantity eaches(int amount) {
        return new Quantity(BigDecimal.valueOf(amount), "EA");
    }

    public static Quantity eaches(BigDecimal amount) {
        return new Quantity(amount, "EA");
    }

    public static Quantity cases(int amount) {
        return new Quantity(BigDecimal.valueOf(amount), "CS");
    }

    public static Quantity zero(String uom) {
        return new Quantity(BigDecimal.ZERO, uom);
    }

    public BigDecimal getValue() {
        return value;
    }

    public String getUom() {
        return uom;
    }

    public Quantity add(Quantity other) {
        validateSameUom(other);
        return new Quantity(this.value.add(other.value), this.uom);
    }

    public Quantity subtract(Quantity other) {
        validateSameUom(other);
        return new Quantity(this.value.subtract(other.value), this.uom);
    }

    public Quantity multiply(BigDecimal multiplier) {
        return new Quantity(this.value.multiply(multiplier), this.uom);
    }

    public Quantity divide(BigDecimal divisor) {
        return new Quantity(this.value.divide(divisor, 4, RoundingMode.HALF_UP), this.uom);
    }

    public boolean isZero() {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositive() {
        return value.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegative() {
        return value.compareTo(BigDecimal.ZERO) < 0;
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

    private void validateSameUom(Quantity other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot compare with null quantity");
        }
        if (!this.uom.equals(other.uom)) {
            throw new IllegalArgumentException(
                    "Cannot operate on quantities with different UOMs: " + this.uom + " vs " + other.uom);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quantity quantity = (Quantity) o;
        return value.compareTo(quantity.value) == 0 && Objects.equals(uom, quantity.uom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, uom);
    }

    @Override
    public String toString() {
        return value.toPlainString() + " " + uom;
    }
}
