package com.maersk.wms.picking.plugin;

import com.maersk.wms.picking.domain.PickConfirmation;
import com.maersk.wms.picking.domain.PickTask;
import com.maersk.wms.picking.plugin.context.PluginContext;

/**
 * Plugin interface for pick confirmation operations.
 *
 * Replaces legacy SPs: rdt_839Confirm01 through rdt_839Confirm15
 *
 * Confirm plugins are invoked when:
 * - Operator confirms a pick
 * - Short pick processing
 * - Full pick completion
 * - Inventory movement recording
 *
 * Variant mapping (legacy SP → plugin variant):
 * ─────────────────────────────────────────────
 * rdt_839Confirm01   → STANDARD_CONFIRM
 * rdt_839Confirm02   → SERIAL_TRACKED
 * rdt_839Confirm03   → LOT_CONTROLLED
 * rdt_839Confirm04   → SHORT_PICK_HANDLER
 * rdt_839Confirm05   → MULTI_LPN_CONFIRM
 */
public interface ConfirmPlugin extends PickingPlugin {

    /**
     * Pre-confirm validation.
     *
     * @param confirmation Confirmation details
     * @param task Current task
     * @param context Plugin context
     * @return Plugin result with validation status
     */
    default PluginResult preConfirm(PickConfirmation confirmation, PickTask task, PluginContext context) {
        return PluginResult.success();
    }

    /**
     * Post-confirm processing.
     * Called after pick is confirmed in database.
     *
     * @param confirmation Confirmation details
     * @param task Updated task
     * @param context Plugin context
     * @return Plugin result
     */
    default PluginResult postConfirm(PickConfirmation confirmation, PickTask task, PluginContext context) {
        return PluginResult.success();
    }

    /**
     * Short pick handling.
     * Called when picked quantity < requested quantity.
     *
     * @param confirmation Confirmation with short pick details
     * @param task Current task
     * @param context Plugin context
     * @return Plugin result with short pick handling
     */
    default PluginResult onShortPick(PickConfirmation confirmation, PickTask task, PluginContext context) {
        return PluginResult.success();
    }
}
