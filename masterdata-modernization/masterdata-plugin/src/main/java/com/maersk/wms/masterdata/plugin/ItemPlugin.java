package com.maersk.wms.masterdata.plugin;

import com.maersk.wms.masterdata.domain.Item;

/**
 * Plugin interface for item/SKU operations.
 * Allows client-specific customizations for item master data.
 */
public interface ItemPlugin extends MasterDataPlugin {

    /**
     * Called before item creation.
     */
    default PluginResult beforeItemCreate(Item item, MasterDataPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after item creation.
     */
    default PluginResult afterItemCreate(Item item, MasterDataPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called before item update.
     */
    default PluginResult beforeItemUpdate(Item item, MasterDataPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after item update.
     */
    default PluginResult afterItemUpdate(Item item, MasterDataPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Validate item data.
     */
    default PluginResult validateItem(Item item, MasterDataPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Transform/enrich item data before save.
     */
    default Item transformItem(Item item, MasterDataPluginContext context) {
        return item;
    }

    /**
     * Determine default storage zone for item.
     */
    default String determineStorageZone(Item item, MasterDataPluginContext context) {
        return item.getStorageZone();
    }

    /**
     * Determine ABC classification for item.
     */
    default String determineAbcClass(Item item, MasterDataPluginContext context) {
        return item.getAbcClass();
    }
}
