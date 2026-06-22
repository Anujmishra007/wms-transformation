package com.maersk.wms.inventory.plugin;

import com.maersk.wms.inventory.domain.InventoryTransfer;
import com.maersk.wms.inventory.plugin.context.InventoryPluginContext;

/**
 * Plugin interface for inventory transfer operations.
 */
public interface TransferPlugin extends InventoryPlugin {

    /**
     * Pre-transfer validation.
     */
    default PluginResult preTransfer(InventoryTransfer transfer, InventoryPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Post-transfer processing.
     */
    default PluginResult postTransfer(InventoryTransfer transfer, InventoryPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Validate destination location.
     */
    default PluginResult validateDestination(String toLocation, InventoryTransfer transfer, InventoryPluginContext context) {
        return PluginResult.success();
    }
}
