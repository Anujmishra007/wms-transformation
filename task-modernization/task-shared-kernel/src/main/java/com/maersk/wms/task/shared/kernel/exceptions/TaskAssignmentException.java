package com.maersk.wms.task.shared.kernel.exceptions;

/**
 * Exception thrown when task assignment fails.
 */
public class TaskAssignmentException extends TaskManagementException {

    public TaskAssignmentException(String message) {
        super("TASK_ASSIGNMENT_FAILED", message);
    }
}
