package com.maersk.wms.task.plugin;

import com.maersk.wms.task.domain.entity.Task;
import com.maersk.wms.task.domain.enums.TaskPriority;

/**
 * Plugin interface for task creation customization.
 */
public interface TaskCreationPlugin extends TaskPlugin {

    /**
     * Called before a task is created.
     * Can modify task attributes or prevent creation.
     */
    default PluginResult beforeTaskCreate(Task task, TaskPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after a task is created.
     * Can trigger additional actions or notifications.
     */
    default PluginResult afterTaskCreate(Task task, TaskPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Validates task before creation.
     */
    default PluginResult validateTask(Task task, TaskPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Transforms task attributes before persistence.
     */
    default Task transformTask(Task task, TaskPluginContext context) {
        return task;
    }

    /**
     * Determines task priority based on client rules.
     */
    default TaskPriority determinePriority(Task task, TaskPluginContext context) {
        return task.getPriority();
    }

    /**
     * Determines the work group for task assignment.
     */
    default String determineWorkGroup(Task task, TaskPluginContext context) {
        return task.getWorkGroup();
    }

    /**
     * Determines the work zone for task assignment.
     */
    default String determineWorkZone(Task task, TaskPluginContext context) {
        return task.getWorkZone();
    }
}
