package com.maersk.wms.outbound.plugin;

/**
 * Base interface for all outbound plugins.
 * Plugins provide client-specific customizations for outbound operations.
 */
public interface OutboundPlugin {

    /**
     * Get the client code this plugin applies to.
     * Return "*" for default plugins that apply to all clients.
     */
    String getClientCode();

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
    default boolean isEnabled(OutboundPluginContext context) {
        return true;
    }
}
