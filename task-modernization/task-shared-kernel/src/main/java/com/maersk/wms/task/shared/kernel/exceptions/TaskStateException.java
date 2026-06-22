package com.maersk.wms.task.shared.kernel.exceptions;

/**
 * Exception thrown when task state transition is invalid.
 */
public class TaskStateException extends TaskManagementException {

    public TaskStateException(String currentState, String targetState) {
        super("INVALID_STATE_TRANSITION",
              "Cannot transition task from " + currentState + " to " + targetState);
    }

    public TaskStateException(String message) {
        super("INVALID_STATE", message);
    }
}
