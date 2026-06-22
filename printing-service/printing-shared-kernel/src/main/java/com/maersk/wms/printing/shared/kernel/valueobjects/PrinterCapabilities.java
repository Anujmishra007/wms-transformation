package com.maersk.wms.printing.shared.kernel.valueobjects;

import java.util.List;
import java.util.Set;

/**
 * Value object representing printer capabilities.
 */
public record PrinterCapabilities(
        Set<String> supportedLabelTypes,
        Set<String> supportedFormats,
        int maxDpi,
        int maxWidthMm,
        int maxHeightMm,
        boolean supportsDuplex,
        boolean supportsColor,
        boolean supportsBarcodes,
        List<String> supportedBarcodeTypes
) {
    public static PrinterCapabilities zebraDefault() {
        return new PrinterCapabilities(
                Set.of("SHIPPING", "LPN", "LOCATION", "CONTENT"),
                Set.of("ZPL", "EPL"),
                300,
                104,
                152,
                false,
                false,
                true,
                List.of("CODE128", "CODE39", "EAN13", "UPC", "QR", "DATAMATRIX")
        );
    }

    public static PrinterCapabilities laserDefault() {
        return new PrinterCapabilities(
                Set.of("DOCUMENT", "PICK_LIST", "PACK_SLIP", "BOL"),
                Set.of("PDF", "PCL", "POSTSCRIPT"),
                600,
                215,
                355,
                true,
                true,
                true,
                List.of("CODE128", "CODE39", "QR")
        );
    }

    public boolean supportsLabelType(String labelType) {
        return supportedLabelTypes.contains(labelType);
    }

    public boolean supportsFormat(String format) {
        return supportedFormats.contains(format.toUpperCase());
    }

    public boolean supportsBarcodeType(String barcodeType) {
        return supportedBarcodeTypes.contains(barcodeType.toUpperCase());
    }
}
