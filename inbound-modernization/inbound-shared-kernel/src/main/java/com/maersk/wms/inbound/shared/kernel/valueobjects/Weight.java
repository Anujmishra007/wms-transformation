package com.maersk.wms.inbound.shared.kernel.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value object representing a weight measurement.
 * Immutable and used across all inbound subdomains.
 *
 * Part of Shared Kernel - can be used by all bounded contexts.
 */
public final class Weight {

    private final BigDecimal value;
    private final String uom;

    public Weight(BigDecimal value, String uom) {
        if (value == null) {
            throw new IllegalArgumentException("Weight value cannot be null");
        }
        if (uom == null || uom.isBlank()) {
            throw new IllegalArgumentException("Weight UOM cannot be null or blank");
        }
        this.value = value;
        this.uom = uom;
    }

    public static Weight of(BigDecimal value, String uom) {
        return new Weight(value, uom);
    }

    public static Weight kilograms(BigDecimal value) {
        return new Weight(value, "KG");
    }

    public static Weight pounds(BigDecimal value) {
        return new Weight(value, "LB");
    }

    public static Weight zero(String uom) {
        return new Weight(BigDecimal.ZERO, uom);
    }

    public BigDecimal getValue() {
        return value;
    }

    public String getUom() {
        return uom;
    }

    public Weight add(Weight other) {
        validateSameUom(other);
        return new Weight(this.value.add(other.value), this.uom);
    }

    public Weight subtract(Weight other) {
        validateSameUom(other);
        return new Weight(this.value.subtract(other.value), this.uom);
    }

    public Weight multiply(BigDecimal multiplier) {
        return new Weight(this.value.multiply(multiplier), this.uom);
    }

    public boolean isZero() {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    private void validateSameUom(Weight other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot compare with null weight");
        }
        if (!this.uom.equals(other.uom)) {
            throw new IllegalArgumentException(
                    "Cannot operate on weights with different UOMs: " + this.uom + " vs " + other.uom);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Weight weight = (Weight) o;
        return value.compareTo(weight.value) == 0 && Objects.equals(uom, weight.uom);
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
