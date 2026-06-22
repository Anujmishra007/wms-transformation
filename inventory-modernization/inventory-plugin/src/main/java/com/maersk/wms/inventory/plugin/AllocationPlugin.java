package com.maersk.wms.inventory.plugin;

import com.maersk.wms.inventory.domain.LotxLocxId;
import com.maersk.wms.inventory.plugin.context.InventoryPluginContext;
import java.math.BigDecimal;
import java.util.List;

/**
 * Plugin interface for inventory allocation operations.
 * Supports 20+ FIFO variants.
 */
public interface AllocationPlugin extends InventoryPlugin {

    /**
     * Pre-allocation validation.
     */
    default PluginResult preAllocate(String sku, BigDecimal qty, InventoryPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Custom allocation sorting/prioritization.
     * Override to implement client-specific FIFO variants.
     */
    default List<LotxLocxId> sortForAllocation(List<LotxLocxId> inventory, InventoryPluginContext context) {
        return inventory; // Default: use standard sort
    }

    /**
     * Post-allocation processing.
     */
    default PluginResult postAllocate(List<LotxLocxId> allocated, InventoryPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Custom lottable matching for allocation.
     */
    default PluginResult matchLottables(LotxLocxId inventory, java.util.Map<String, String> requiredLottables,
                                        InventoryPluginContext context) {
        return PluginResult.success();
    }
}
