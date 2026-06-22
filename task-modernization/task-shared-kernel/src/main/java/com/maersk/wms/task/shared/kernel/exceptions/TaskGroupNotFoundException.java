package com.maersk.wms.task.shared.kernel.exceptions;

/**
 * Exception thrown when a task group is not found.
 */
public class TaskGroupNotFoundException extends TaskManagementException {

    public TaskGroupNotFoundException(String groupKey) {
        super("TASK_GROUP_NOT_FOUND", "Task group not found: " + groupKey);
    }
}
