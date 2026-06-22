package com.maersk.wms.inventory.plugin;

import com.maersk.wms.inventory.domain.InventoryAdjustment;
import com.maersk.wms.inventory.plugin.context.InventoryPluginContext;

/**
 * Plugin interface for inventory adjustment operations.
 */
public interface AdjustmentPlugin extends InventoryPlugin {

    /**
     * Pre-adjustment validation.
     */
    default PluginResult preAdjustment(InventoryAdjustment adjustment, InventoryPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Post-adjustment processing.
     */
    default PluginResult postAdjustment(InventoryAdjustment adjustment, InventoryPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Custom variance threshold check.
     */
    default PluginResult checkVarianceThreshold(InventoryAdjustment adjustment, InventoryPluginContext context) {
        return PluginResult.success();
    }
}
