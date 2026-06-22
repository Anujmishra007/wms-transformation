package com.maersk.wms.picking.shared.kernel.valueobjects;

import lombok.Value;
import java.math.BigDecimal;

/**
 * Value object representing a quantity with unit of measure.
 */
@Value
public class Quantity {
    BigDecimal value;
    String uom;

    public static Quantity of(BigDecimal value, String uom) {
        return new Quantity(value, uom);
    }

    public static Quantity of(int value, String uom) {
        return new Quantity(BigDecimal.valueOf(value), uom);
    }

    public static Quantity zero(String uom) {
        return new Quantity(BigDecimal.ZERO, uom);
    }

    public BigDecimal getValue() {
        return value;
    }

    public boolean isZero() {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositive() {
        return value.compareTo(BigDecimal.ZERO) > 0;
    }

    public Quantity add(Quantity other) {
        if (!this.uom.equals(other.uom)) {
            throw new IllegalArgumentException("Cannot add quantities with different UOMs");
        }
        return new Quantity(this.value.add(other.value), this.uom);
    }

    public Quantity subtract(Quantity other) {
        if (!this.uom.equals(other.uom)) {
            throw new IllegalArgumentException("Cannot subtract quantities with different UOMs");
        }
        return new Quantity(this.value.subtract(other.value), this.uom);
    }
}
