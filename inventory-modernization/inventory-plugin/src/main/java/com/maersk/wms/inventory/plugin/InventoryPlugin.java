package com.maersk.wms.inventory.plugin;

import com.maersk.wms.inventory.plugin.context.InventoryPluginContext;

/**
 * Base interface for all inventory plugins.
 * Replaces legacy extension SPs for inventory operations.
 */
public interface InventoryPlugin {

    /**
     * Get unique plugin identifier.
     */
    String getPluginId();

    /**
     * Check if this plugin applies to the given context.
     */
    boolean appliesTo(InventoryPluginContext context);

    /**
     * Get execution order (lower = earlier).
     */
    default int getOrder() {
        return 100;
    }

    /**
     * Check if plugin is enabled.
     */
    default boolean isEnabled() {
        return true;
    }

    /**
     * Get the legacy SP this plugin replaces.
     */
    default String getLegacySP() {
        return null;
    }
}
