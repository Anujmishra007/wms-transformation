package com.maersk.wms.task.shared.kernel.exceptions;

/**
 * Exception thrown when task grouping operations fail.
 */
public class TaskGroupException extends TaskManagementException {

    public TaskGroupException(String message) {
        super("TASK_GROUP_ERROR", message);
    }
}
