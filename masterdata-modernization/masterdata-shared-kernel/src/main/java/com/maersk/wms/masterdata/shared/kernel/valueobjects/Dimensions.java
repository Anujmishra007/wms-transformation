package com.maersk.wms.masterdata.shared.kernel.valueobjects;

import java.math.BigDecimal;

/**
 * Value object representing physical dimensions.
 */
public record Dimensions(
        BigDecimal length,
        BigDecimal width,
        BigDecimal height,
        BigDecimal weight,
        String dimensionUom,
        String weightUom
) {
    public Dimensions {
        if (length != null && length.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Length cannot be negative");
        }
        if (width != null && width.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Width cannot be negative");
        }
        if (height != null && height.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Height cannot be negative");
        }
        if (weight != null && weight.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Weight cannot be negative");
        }
    }

    public BigDecimal volume() {
        if (length == null || width == null || height == null) {
            return null;
        }
        return length.multiply(width).multiply(height);
    }

    public static Dimensions empty() {
        return new Dimensions(null, null, null, null, null, null);
    }

    public static Dimensions of(BigDecimal length, BigDecimal width, BigDecimal height, BigDecimal weight) {
        return new Dimensions(length, width, height, weight, "CM", "KG");
    }
}
