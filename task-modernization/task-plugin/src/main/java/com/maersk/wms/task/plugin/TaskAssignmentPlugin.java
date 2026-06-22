package com.maersk.wms.task.plugin;

import com.maersk.wms.task.domain.entity.Task;
import com.maersk.wms.task.domain.entity.TaskAssignment;
import com.maersk.wms.task.domain.entity.UserWorkload;

import java.util.List;

/**
 * Plugin interface for task assignment customization.
 */
public interface TaskAssignmentPlugin extends TaskPlugin {

    /**
     * Called before a task is assigned to a user.
     */
    default PluginResult beforeAssignment(Task task, String userId, TaskPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after a task is assigned to a user.
     */
    default PluginResult afterAssignment(TaskAssignment assignment, TaskPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Validates if a user can be assigned the task.
     */
    default PluginResult validateAssignment(Task task, String userId, TaskPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Selects the best user for task assignment from available users.
     */
    default String selectUser(Task task, List<UserWorkload> availableUsers, TaskPluginContext context) {
        // Default: return first available user
        return availableUsers.isEmpty() ? null : availableUsers.get(0).getUserId();
    }

    /**
     * Calculates assignment priority for a user-task combination.
     */
    default int calculateAssignmentScore(Task task, UserWorkload user, TaskPluginContext context) {
        return 0;
    }

    /**
     * Called before a task is unassigned/released.
     */
    default PluginResult beforeUnassignment(Task task, String reason, TaskPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after a task is unassigned/released.
     */
    default PluginResult afterUnassignment(Task task, String reason, TaskPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Determines if auto-assignment should be used for this task.
     */
    default boolean shouldAutoAssign(Task task, TaskPluginContext context) {
        return true;
    }
}
