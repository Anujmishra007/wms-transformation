package com.maersk.wms.picking.shared.kernel.exceptions;

/**
 * Exception thrown when a pick task is not found.
 */
public class TaskNotFoundException extends PickingException {

    public TaskNotFoundException(String taskKey) {
        super("TASK_NOT_FOUND",
                String.format("Pick task not found: %s", taskKey));
    }

    public static TaskNotFoundException byKey(String taskKey) {
        return new TaskNotFoundException(taskKey);
    }
}
