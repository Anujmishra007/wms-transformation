package com.maersk.wms.inventory.plugin;

import com.maersk.wms.inventory.domain.InventoryHold;
import com.maersk.wms.inventory.plugin.context.InventoryPluginContext;

/**
 * Plugin interface for inventory hold operations.
 */
public interface HoldPlugin extends InventoryPlugin {

    /**
     * Pre-hold validation.
     */
    default PluginResult preHold(InventoryHold hold, InventoryPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Post-hold processing.
     */
    default PluginResult postHold(InventoryHold hold, InventoryPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Pre-release validation.
     */
    default PluginResult preRelease(InventoryHold hold, InventoryPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Post-release processing.
     */
    default PluginResult postRelease(InventoryHold hold, InventoryPluginContext context) {
        return PluginResult.success();
    }
}
