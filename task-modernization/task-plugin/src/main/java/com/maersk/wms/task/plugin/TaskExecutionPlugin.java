package com.maersk.wms.task.plugin;

import com.maersk.wms.task.domain.entity.Task;

/**
 * Plugin interface for task execution customization.
 */
public interface TaskExecutionPlugin extends TaskPlugin {

    /**
     * Called when a task is started.
     */
    default PluginResult onTaskStarted(Task task, TaskPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called when a task is completed.
     */
    default PluginResult onTaskCompleted(Task task, TaskPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called when a task is partially completed.
     */
    default PluginResult onTaskPartiallyCompleted(Task task, Double completedQuantity, TaskPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called when a task is shorted (completed with shortage).
     */
    default PluginResult onTaskShorted(Task task, Double shortQuantity, String reason, TaskPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called when a task is cancelled.
     */
    default PluginResult onTaskCancelled(Task task, String reason, TaskPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called when a task fails.
     */
    default PluginResult onTaskFailed(Task task, String errorMessage, TaskPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called when a task is put on hold.
     */
    default PluginResult onTaskOnHold(Task task, String reason, TaskPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Validates task completion.
     */
    default PluginResult validateCompletion(Task task, Double completedQuantity, TaskPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Determines if shortage is allowed for this task.
     */
    default boolean allowShortage(Task task, TaskPluginContext context) {
        return true;
    }

    /**
     * Calculates actual time for task completion.
     */
    default Integer calculateActualTime(Task task, TaskPluginContext context) {
        if (task.getStartedAt() != null && task.getCompletedAt() != null) {
            return (int) java.time.Duration.between(task.getStartedAt(), task.getCompletedAt()).toMinutes();
        }
        return null;
    }
}
