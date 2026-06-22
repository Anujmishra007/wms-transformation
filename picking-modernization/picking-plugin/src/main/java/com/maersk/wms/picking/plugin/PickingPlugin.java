package com.maersk.wms.picking.plugin;

import com.maersk.wms.picking.plugin.context.PluginContext;

/**
 * Base interface for all picking plugins.
 * Replaces legacy extension SPs: rdt_839*, nsp_839*, isp_839*
 *
 * Plugin Categories (mapped from legacy SPs):
 * ─────────────────────────────────────────────
 * rdt_839GetTaskSP01-17     → GetTaskPlugin (17 variants)
 * rdt_839DecodeSP01-09      → DecodePlugin (9 variants)
 * rdt_839Confirm01-15       → ConfirmPlugin (15 variants)
 * rdt_839ExtValidSP01-16    → ExtendedValidationPlugin (16 variants)
 * rdt_839ExtUpd01-10        → ExtendedUpdatePlugin (10 variants)
 * rdt_839ExtInfo01-12       → ExtendedInfoPlugin (12 variants)
 */
public interface PickingPlugin {

    /**
     * Get unique plugin identifier.
     * Convention: {operation}-{client}-{region} e.g., "gettask-nike-kr"
     */
    String getPluginId();

    /**
     * Check if this plugin applies to the given context.
     * Evaluated at runtime based on client, region, warehouse, etc.
     */
    boolean appliesTo(PluginContext context);

    /**
     * Get execution order (lower = earlier execution).
     * Default: 100
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
     * Get the legacy SP this plugin replaces (for parity testing).
     */
    default String getLegacySP() {
        return null;
    }
}
