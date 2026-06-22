package com.maersk.wms.printing.shared.kernel.valueobjects;

import java.math.BigDecimal;

/**
 * Value object representing label physical dimensions.
 */
public record LabelDimensions(
        BigDecimal widthMm,
        BigDecimal heightMm,
        String orientation
) {
    public LabelDimensions {
        if (widthMm != null && widthMm.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Width must be positive");
        }
        if (heightMm != null && heightMm.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Height must be positive");
        }
        if (orientation == null) {
            orientation = "PORTRAIT";
        }
    }

    public static LabelDimensions standard4x6() {
        return new LabelDimensions(
                BigDecimal.valueOf(101.6), // 4 inches
                BigDecimal.valueOf(152.4), // 6 inches
                "PORTRAIT"
        );
    }

    public static LabelDimensions standard2x1() {
        return new LabelDimensions(
                BigDecimal.valueOf(50.8), // 2 inches
                BigDecimal.valueOf(25.4), // 1 inch
                "LANDSCAPE"
        );
    }

    public boolean isLandscape() {
        return "LANDSCAPE".equalsIgnoreCase(orientation);
    }
}
