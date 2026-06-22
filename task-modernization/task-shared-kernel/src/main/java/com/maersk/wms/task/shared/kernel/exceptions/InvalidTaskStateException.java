package com.maersk.wms.task.shared.kernel.exceptions;

/**
 * Exception thrown when task state transition is invalid.
 */
public class InvalidTaskStateException extends TaskManagementException {

    public InvalidTaskStateException(String message) {
        super("INVALID_TASK_STATE", message);
    }

    public InvalidTaskStateException(String currentState, String targetState) {
        super("INVALID_STATE_TRANSITION",
              "Cannot transition task from " + currentState + " to " + targetState);
    }
}
