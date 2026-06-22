package com.maersk.wms.printing.shared.kernel.valueobjects;

import java.util.Map;

/**
 * Value object representing data to be printed on a label.
 */
public record LabelData(
        String sourceType,
        String sourceKey,
        Map<String, String> fields,
        Map<String, String> barcodes,
        Map<String, byte[]> images
) {
    public LabelData {
        if (fields == null) {
            fields = Map.of();
        }
        if (barcodes == null) {
            barcodes = Map.of();
        }
        if (images == null) {
            images = Map.of();
        }
    }

    public static LabelData empty() {
        return new LabelData(null, null, Map.of(), Map.of(), Map.of());
    }

    public String getField(String fieldName) {
        return fields.get(fieldName);
    }

    public String getBarcode(String barcodeName) {
        return barcodes.get(barcodeName);
    }

    public boolean hasField(String fieldName) {
        return fields.containsKey(fieldName);
    }

    public boolean hasBarcode(String barcodeName) {
        return barcodes.containsKey(barcodeName);
    }

    public int fieldCount() {
        return fields.size();
    }
}
