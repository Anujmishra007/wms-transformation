package com.maersk.wms.picking.plugin;

import com.maersk.wms.picking.domain.BarcodeType;
import com.maersk.wms.picking.domain.DecodeResult;
import com.maersk.wms.picking.domain.PickTask;
import com.maersk.wms.picking.plugin.context.PluginContext;

/**
 * Plugin interface for barcode decode operations.
 *
 * Replaces legacy SPs: rdt_839DecodeSP01 through rdt_839DecodeSP09
 *
 * Decode plugins are invoked when:
 * - Operator scans a barcode during picking
 * - Barcode validation is required
 * - Client-specific barcode formats need parsing
 *
 * Variant mapping (legacy SP → plugin variant):
 * ─────────────────────────────────────────────
 * rdt_839DecodeSP01  → STANDARD_DECODE
 * rdt_839DecodeSP02  → GS1_128_DECODE
 * rdt_839DecodeSP03  → NIKE_CUSTOM_SKU
 * rdt_839DecodeSP04  → HM_LOCATION
 * rdt_839DecodeSP05  → SERIAL_DECODE
 */
public interface DecodePlugin extends PickingPlugin {

    /**
     * Pre-decode hook - modify barcode before decoding.
     *
     * @param barcode Raw barcode string
     * @param expectedType Expected barcode type
     * @param context Plugin context
     * @return Plugin result with modified barcode
     */
    default PluginResult preDecode(String barcode, BarcodeType expectedType, PluginContext context) {
        return PluginResult.success();
    }

    /**
     * Custom decode logic for client-specific barcodes.
     *
     * @param barcode Barcode to decode
     * @param expectedType Expected type
     * @param task Current task context
     * @param context Plugin context
     * @return Decode result or null to use default decoding
     */
    default DecodeResult decode(String barcode, BarcodeType expectedType, PickTask task, PluginContext context) {
        return null; // Use default decoder
    }

    /**
     * Post-decode validation - validate decoded result.
     *
     * @param result Decoded result
     * @param task Current task
     * @param context Plugin context
     * @return Plugin result with validation status
     */
    default PluginResult postDecode(DecodeResult result, PickTask task, PluginContext context) {
        return PluginResult.success();
    }
}
