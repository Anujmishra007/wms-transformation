package com.maersk.wms.outbound.plugin;

import com.maersk.wms.outbound.domain.Order;
import com.maersk.wms.outbound.domain.Wave;

import java.util.List;

/**
 * Plugin interface for wave planning operations.
 * Allows client-specific customizations for order waving.
 */
public interface WavePlugin extends OutboundPlugin {

    /**
     * Filter orders eligible for waving.
     */
    default List<Order> filterOrdersForWave(List<Order> orders, OutboundPluginContext context) {
        return orders;
    }

    /**
     * Group orders into waves.
     */
    default List<List<Order>> groupOrdersIntoWaves(List<Order> orders, OutboundPluginContext context) {
        return List.of(orders);
    }

    /**
     * Called before wave is created.
     */
    default PluginResult beforeWaveCreate(Wave wave, List<Order> orders, OutboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after wave is created.
     */
    default PluginResult afterWaveCreate(Wave wave, OutboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called before wave is released.
     */
    default PluginResult beforeWaveRelease(Wave wave, OutboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after wave is released.
     */
    default PluginResult afterWaveRelease(Wave wave, OutboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Determine wave priority.
     */
    default int calculateWavePriority(Wave wave, OutboundPluginContext context) {
        return 5;
    }
}
