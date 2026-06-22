package com.maersk.wms.picking.plugin;

import com.maersk.wms.picking.domain.PickTask;
import com.maersk.wms.picking.plugin.context.PluginContext;
import java.util.List;

/**
 * Plugin interface for GetTask operations.
 *
 * Replaces legacy SPs: rdt_839GetTaskSP01 through rdt_839GetTaskSP17
 *
 * GetTask plugins are invoked when:
 * - Operator requests next task (Screen 4640 - Task Menu)
 * - Task assignment optimization is needed
 * - Zone/aisle filtering is required
 *
 * Variant mapping (legacy SP → plugin variant):
 * ─────────────────────────────────────────────
 * rdt_839GetTaskSP01  → NIKE_STANDARD
 * rdt_839GetTaskSP02  → NIKE_PRIORITY
 * rdt_839GetTaskSP03  → ADIDAS_WAVE
 * rdt_839GetTaskSP04  → HM_ZONE
 * rdt_839GetTaskSP05  → UNILEVER_BATCH
 */
public interface GetTaskPlugin extends PickingPlugin {

    /**
     * Pre-process before task retrieval.
     * Can modify task query criteria.
     *
     * @param criteria Task query criteria
     * @param context Plugin context
     * @return Plugin result with modified criteria
     */
    default PluginResult preGetTask(GetTaskCriteria criteria, PluginContext context) {
        return PluginResult.success();
    }

    /**
     * Post-process after task retrieval.
     * Can filter, sort, or enrich task results.
     *
     * @param tasks Retrieved tasks
     * @param context Plugin context
     * @return Plugin result with modified task list
     */
    default PluginResult postGetTask(List<PickTask> tasks, PluginContext context) {
        return PluginResult.success();
    }

    /**
     * Customize task assignment logic.
     * Called when assigning a specific task to operator.
     *
     * @param task Task being assigned
     * @param context Plugin context
     * @return Plugin result indicating if assignment should proceed
     */
    default PluginResult onTaskAssignment(PickTask task, PluginContext context) {
        return PluginResult.success();
    }
}
