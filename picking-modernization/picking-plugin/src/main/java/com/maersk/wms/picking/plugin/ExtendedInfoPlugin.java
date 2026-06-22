package com.maersk.wms.picking.plugin;

import com.maersk.wms.picking.domain.PickTask;
import com.maersk.wms.picking.plugin.context.PluginContext;
import java.util.Map;

/**
 * Plugin interface for extended information retrieval.
 *
 * Replaces legacy SPs: rdt_839ExtInfo01 through rdt_839ExtInfo12
 *
 * Extended info plugins are invoked for:
 * - Client-specific display information
 * - Additional product details
 * - Location guidance
 * - Pick instructions
 *
 * Variant mapping (legacy SP → plugin variant):
 * ─────────────────────────────────────────────
 * rdt_839ExtInfo01  → PRODUCT_DETAILS
 * rdt_839ExtInfo02  → LOCATION_GUIDANCE
 * rdt_839ExtInfo03  → PICK_INSTRUCTIONS
 * rdt_839ExtInfo04  → HAZMAT_INFO
 * rdt_839ExtInfo05  → BUNDLE_INFO
 */
public interface ExtendedInfoPlugin extends PickingPlugin {

    /**
     * Get additional task display information.
     *
     * @param task Task to get info for
     * @param context Plugin context
     * @return Plugin result with additional info in data map
     */
    default PluginResult getTaskInfo(PickTask task, PluginContext context) {
        return PluginResult.success();
    }

    /**
     * Get product-specific information.
     *
     * @param sku SKU to get info for
     * @param task Current task
     * @param context Plugin context
     * @return Plugin result with product info
     */
    default PluginResult getProductInfo(String sku, PickTask task, PluginContext context) {
        return PluginResult.success();
    }

    /**
     * Get location guidance information.
     *
     * @param location Location
     * @param task Current task
     * @param context Plugin context
     * @return Plugin result with guidance info
     */
    default PluginResult getLocationGuidance(String location, PickTask task, PluginContext context) {
        return PluginResult.success();
    }

    /**
     * Get handling instructions.
     *
     * @param task Current task
     * @param context Plugin context
     * @return Plugin result with handling instructions
     */
    default PluginResult getHandlingInstructions(PickTask task, PluginContext context) {
        return PluginResult.success();
    }

    /**
     * Customize RF display data.
     *
     * @param screenId Screen being displayed
     * @param displayData Current display data
     * @param task Current task
     * @param context Plugin context
     * @return Plugin result with modified display data
     */
    default PluginResult customizeDisplay(String screenId, Map<String, Object> displayData,
                                          PickTask task, PluginContext context) {
        return PluginResult.success();
    }
}
