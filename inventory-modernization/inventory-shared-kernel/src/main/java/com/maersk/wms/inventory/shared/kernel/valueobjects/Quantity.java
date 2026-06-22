package com.maersk.wms.inventory.shared.kernel.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Value object representing inventory quantity.
 * Immutable and supports quantity operations.
 */
public record Quantity(BigDecimal value, String uom) {

    public static final Quantity ZERO = new Quantity(BigDecimal.ZERO, "EA");

    public Quantity {
        if (value == null) {
            throw new IllegalArgumentException("Quantity value cannot be null");
        }
        if (uom == null || uom.isBlank()) {
            uom = "EA"; // Default unit of measure
        }
        value = value.setScale(4, RoundingMode.HALF_UP);
    }

    public static Quantity of(double value, String uom) {
        return new Quantity(BigDecimal.valueOf(value), uom);
    }

    public static Quantity of(BigDecimal value, String uom) {
        return new Quantity(value, uom);
    }

    public static Quantity each(double value) {
        return new Quantity(BigDecimal.valueOf(value), "EA");
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

    public boolean isPositive() {
        return value.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegative() {
        return value.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isZero() {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isGreaterThan(Quantity other) {
        validateSameUom(other);
        return this.value.compareTo(other.value) > 0;
    }

    public boolean isLessThan(Quantity other) {
        validateSameUom(other);
        return this.value.compareTo(other.value) < 0;
    }

    public boolean isGreaterThanOrEqual(Quantity other) {
        validateSameUom(other);
        return this.value.compareTo(other.value) >= 0;
    }

    private void validateSameUom(Quantity other) {
        if (!this.uom.equalsIgnoreCase(other.uom)) {
            throw new IllegalArgumentException(
                    "Cannot operate on quantities with different UOM: " + this.uom + " vs " + other.uom);
        }
    }
}
