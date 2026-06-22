package com.maersk.wms.printing.shared.kernel.valueobjects;

/**
 * Value object representing print job settings.
 */
public record PrintSettings(
        int copies,
        int dpi,
        String mediaType,
        String orientation,
        boolean collate,
        int darkness,
        int speed
) {
    public PrintSettings {
        if (copies <= 0) {
            copies = 1;
        }
        if (dpi <= 0) {
            dpi = 203; // Standard thermal printer DPI
        }
        if (darkness < 0 || darkness > 30) {
            darkness = 15; // Medium darkness
        }
        if (speed < 0 || speed > 14) {
            speed = 4; // Medium speed
        }
    }

    public static PrintSettings defaults() {
        return new PrintSettings(1, 203, "LABEL", "PORTRAIT", false, 15, 4);
    }

    public static PrintSettings highQuality() {
        return new PrintSettings(1, 300, "LABEL", "PORTRAIT", false, 20, 2);
    }

    public static PrintSettings highSpeed() {
        return new PrintSettings(1, 203, "LABEL", "PORTRAIT", false, 10, 8);
    }

    public PrintSettings withCopies(int copies) {
        return new PrintSettings(copies, dpi, mediaType, orientation, collate, darkness, speed);
    }

    public PrintSettings withDpi(int dpi) {
        return new PrintSettings(copies, dpi, mediaType, orientation, collate, darkness, speed);
    }
}
