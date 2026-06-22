package com.maersk.wms.task.plugin;

/**
 * Base interface for all task management plugins.
 * Plugins provide client-specific customization points for task operations.
 */
public interface TaskPlugin {

    /**
     * Returns the client code this plugin applies to.
     * Return "*" for plugins that apply to all clients.
     */
    String getClientCode();

    /**
     * Returns the priority of this plugin (lower = higher priority).
     * Used to determine execution order when multiple plugins match.
     */
    default int getPriority() {
        return 100;
    }

    /**
     * Returns whether this plugin is currently enabled.
     */
    default boolean isEnabled() {
        return true;
    }

    /**
     * Returns the plugin name for logging and diagnostics.
     */
    default String getPluginName() {
        return this.getClass().getSimpleName();
    }
}
