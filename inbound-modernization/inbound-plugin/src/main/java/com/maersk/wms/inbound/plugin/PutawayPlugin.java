package com.maersk.wms.inbound.plugin;

import com.maersk.wms.inbound.domain.PutawayTask;
import com.maersk.wms.inbound.domain.ReceiptDetail;

import java.util.List;

/**
 * Plugin interface for putaway operations.
 * Allows client-specific customizations for putaway location selection and task processing.
 */
public interface PutawayPlugin extends InboundPlugin {

    /**
     * Called to determine putaway location for received inventory.
     * Can override system-suggested location.
     */
    default PluginResult determinePutawayLocation(ReceiptDetail detail,
                                                   String suggestedLocation,
                                                   InboundPluginContext context) {
        return PluginResult.success().withData("location", suggestedLocation);
    }

    /**
     * Called before putaway task is created.
     */
    default PluginResult beforeTaskCreate(PutawayTask task, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after putaway task is created.
     */
    default PluginResult afterTaskCreate(PutawayTask task, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called to prioritize putaway tasks.
     * Can reorder or modify task priorities.
     */
    default List<PutawayTask> prioritizeTasks(List<PutawayTask> tasks, InboundPluginContext context) {
        return tasks;
    }

    /**
     * Called before putaway task is completed.
     */
    default PluginResult beforeTaskComplete(PutawayTask task, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after putaway task is completed.
     */
    default PluginResult afterTaskComplete(PutawayTask task, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Validate putaway location.
     */
    default PluginResult validateLocation(String location, PutawayTask task, InboundPluginContext context) {
        return PluginResult.success();
    }
}
