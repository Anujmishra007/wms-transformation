package com.maersk.wms.masterdata.plugin;

import com.maersk.wms.masterdata.domain.Location;

/**
 * Plugin interface for location operations.
 * Allows client-specific customizations for location master data.
 */
public interface LocationPlugin extends MasterDataPlugin {

    /**
     * Called before location creation.
     */
    default PluginResult beforeLocationCreate(Location location, MasterDataPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after location creation.
     */
    default PluginResult afterLocationCreate(Location location, MasterDataPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called before location update.
     */
    default PluginResult beforeLocationUpdate(Location location, MasterDataPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after location update.
     */
    default PluginResult afterLocationUpdate(Location location, MasterDataPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Validate location data.
     */
    default PluginResult validateLocation(Location location, MasterDataPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Transform/enrich location data before save.
     */
    default Location transformLocation(Location location, MasterDataPluginContext context) {
        return location;
    }

    /**
     * Generate location code based on rules.
     */
    default String generateLocationCode(String zone, String aisle, String bay,
                                         String level, String position,
                                         MasterDataPluginContext context) {
        return zone + "-" + aisle + "-" + bay + "-" + level + "-" + position;
    }

    /**
     * Calculate pick path sequence for location.
     */
    default int calculatePickPathSequence(Location location, MasterDataPluginContext context) {
        return location.getPickPathSequence();
    }
}
