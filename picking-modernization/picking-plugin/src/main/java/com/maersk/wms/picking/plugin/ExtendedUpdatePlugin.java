package com.maersk.wms.picking.plugin;

import com.maersk.wms.picking.domain.PickTask;
import com.maersk.wms.picking.plugin.context.PluginContext;
import java.util.Map;

/**
 * Plugin interface for extended update operations.
 *
 * Replaces legacy SPs: rdt_839ExtUpd01 through rdt_839ExtUpd10
 *
 * Extended update plugins are invoked for:
 * - Client-specific data updates after pick operations
 * - Custom field population
 * - External system updates
 * - Audit trail enrichment
 *
 * Variant mapping (legacy SP → plugin variant):
 * ─────────────────────────────────────────────
 * rdt_839ExtUpd01   → NIKE_CUSTOM_FIELDS
 * rdt_839ExtUpd02   → ADIDAS_TRACKING
 * rdt_839ExtUpd03   → HM_BATCH_UPDATE
 * rdt_839ExtUpd04   → SERIAL_HISTORY
 * rdt_839ExtUpd05   → AUDIT_ENRICHMENT
 */
public interface ExtendedUpdatePlugin extends PickingPlugin {

    /**
     * Update task after pick confirmation.
     *
     * @param task Updated task
     * @param additionalData Additional data from confirmation
     * @param context Plugin context
     * @return Plugin result
     */
    default PluginResult updateAfterPick(PickTask task, Map<String, Object> additionalData, PluginContext context) {
        return PluginResult.success();
    }

    /**
     * Update external systems after pick.
     *
     * @param task Completed task
     * @param context Plugin context
     * @return Plugin result
     */
    default PluginResult updateExternalSystems(PickTask task, PluginContext context) {
        return PluginResult.success();
    }

    /**
     * Enrich audit data.
     *
     * @param task Task being audited
     * @param auditData Audit data to enrich
     * @param context Plugin context
     * @return Plugin result with enriched audit data
     */
    default PluginResult enrichAuditData(PickTask task, Map<String, Object> auditData, PluginContext context) {
        return PluginResult.success();
    }
}
