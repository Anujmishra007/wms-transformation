package com.maersk.wms.picking.domain;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Result of barcode decoding operation.
 *
 * Supports various barcode types:
 * - Location barcodes (A01-02-03 format)
 * - SKU/Item barcodes
 * - LPN (License Plate Number) barcodes
 * - GS1-128 barcodes with embedded data
 */
@Data
@Builder
public class DecodeResult {

    /** Type of barcode decoded */
    private BarcodeType decodedType;

    /** Whether the barcode was successfully validated */
    private boolean validated;

    /** Decoded location value */
    private String location;

    /** Decoded SKU value */
    private String sku;

    /** Decoded LPN value */
    private String lpn;

    /** Decoded lot number */
    private String lot;

    /** Decoded serial number */
    private String serialNumber;

    /** Decoded quantity (from GS1 or UPC) */
    private BigDecimal quantity;

    /** Unit of measure */
    private String uom;

    /** Expiry date extracted from barcode */
    private String expiryDate;

    /** Additional decoded fields (GS1 AIs) */
    private Map<String, String> additionalFields;

    /** Validation message */
    private String message;

    /** Error code if validation failed */
    private String errorCode;

    /** Expected value for comparison */
    private String expectedValue;

    /** Actual decoded value */
    private String actualValue;

    /**
     * Create a successful decode result.
     */
    public static DecodeResult success(BarcodeType type, String value) {
        return DecodeResult.builder()
                .decodedType(type)
                .validated(true)
                .actualValue(value)
                .build();
    }

    /**
     * Create a failed validation result.
     */
    public static DecodeResult validationFailed(BarcodeType type, String expected, String actual, String errorCode) {
        return DecodeResult.builder()
                .decodedType(type)
                .validated(false)
                .expectedValue(expected)
                .actualValue(actual)
                .errorCode(errorCode)
                .message("Validation failed: expected " + expected + " but got " + actual)
                .build();
    }
}
