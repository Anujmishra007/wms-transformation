package com.maersk.wms.picking.plugin;

import com.maersk.wms.picking.domain.PickTask;
import com.maersk.wms.picking.plugin.context.PluginContext;

/**
 * Plugin interface for extended validation operations.
 *
 * Replaces legacy SPs: rdt_839ExtValidSP01 through rdt_839ExtValidSP16
 *
 * Extended validation plugins are invoked for:
 * - Client-specific business rule validation
 * - Lot/serial validation beyond standard checks
 * - Location/zone access validation
 * - User permission validation
 *
 * Variant mapping (legacy SP → plugin variant):
 * ─────────────────────────────────────────────
 * rdt_839ExtValidSP01  → LOT_EXPIRY_CHECK
 * rdt_839ExtValidSP02  → SERIAL_FORMAT_CHECK
 * rdt_839ExtValidSP03  → ZONE_ACCESS_CHECK
 * rdt_839ExtValidSP04  → HAZMAT_VALIDATION
 * rdt_839ExtValidSP05  → WEIGHT_LIMIT_CHECK
 */
public interface ExtendedValidationPlugin extends PickingPlugin {

    /**
     * Validate task before assignment.
     *
     * @param task Task to validate
     * @param context Plugin context
     * @return Plugin result with validation status
     */
    default PluginResult validateTaskAssignment(PickTask task, PluginContext context) {
        return PluginResult.success();
    }

    /**
     * Validate location access.
     *
     * @param location Location to validate
     * @param task Current task
     * @param context Plugin context
     * @return Plugin result with validation status
     */
    default PluginResult validateLocation(String location, PickTask task, PluginContext context) {
        return PluginResult.success();
    }

    /**
     * Validate lot/batch.
     *
     * @param lot Lot number
     * @param task Current task
     * @param context Plugin context
     * @return Plugin result with validation status
     */
    default PluginResult validateLot(String lot, PickTask task, PluginContext context) {
        return PluginResult.success();
    }

    /**
     * Validate serial number.
     *
     * @param serialNumber Serial number
     * @param task Current task
     * @param context Plugin context
     * @return Plugin result with validation status
     */
    default PluginResult validateSerial(String serialNumber, PickTask task, PluginContext context) {
        return PluginResult.success();
    }

    /**
     * Validate quantity.
     *
     * @param quantity Quantity to validate
     * @param task Current task
     * @param context Plugin context
     * @return Plugin result with validation status
     */
    default PluginResult validateQuantity(java.math.BigDecimal quantity, PickTask task, PluginContext context) {
        return PluginResult.success();
    }
}
