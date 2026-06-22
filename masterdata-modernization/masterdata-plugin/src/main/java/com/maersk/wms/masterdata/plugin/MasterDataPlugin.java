package com.maersk.wms.masterdata.plugin;

/**
 * Base interface for all master data plugins.
 * Plugins allow client-specific customizations for master data operations.
 */
public interface MasterDataPlugin {

    /**
     * Get the client code this plugin applies to.
     * Return "*" for plugins that apply to all clients.
     */
    default String getClientCode() {
        return "*";
    }

    /**
     * Get the priority of this plugin.
     * Lower values have higher priority.
     */
    default int getPriority() {
        return 100;
    }

    /**
     * Check if this plugin is enabled for the given context.
     */
    default boolean isEnabled(MasterDataPluginContext context) {
        return true;
    }
}
