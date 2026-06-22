package com.maersk.wms.task.shared.kernel.exceptions;

/**
 * Exception thrown when a task is not found.
 */
public class TaskNotFoundException extends TaskManagementException {

    public TaskNotFoundException(String taskKey) {
        super("TASK_NOT_FOUND", "Task not found: " + taskKey);
    }
}
