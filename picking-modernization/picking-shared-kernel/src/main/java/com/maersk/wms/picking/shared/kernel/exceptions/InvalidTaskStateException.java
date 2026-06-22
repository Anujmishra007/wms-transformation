package com.maersk.wms.picking.shared.kernel.exceptions;

/**
 * Exception thrown when a task is in an invalid state for the requested operation.
 */
public class InvalidTaskStateException extends PickingException {

    public InvalidTaskStateException(String message) {
        super("INVALID_TASK_STATE", message);
    }

    public InvalidTaskStateException(String taskKey, String currentState, String expectedState) {
        super("INVALID_TASK_STATE",
                String.format("Task %s is in state %s, expected %s",
                        taskKey, currentState, expectedState));
    }

    public static InvalidTaskStateException cannotStart(String taskKey, String currentState) {
        return new InvalidTaskStateException(taskKey, currentState, "RELEASED or ASSIGNED");
    }

    public static InvalidTaskStateException cannotConfirm(String taskKey, String currentState) {
        return new InvalidTaskStateException(taskKey, currentState, "IN_PROGRESS");
    }

    public static InvalidTaskStateException cannotCancel(String taskKey, String currentState) {
        return new InvalidTaskStateException(taskKey, currentState, "not COMPLETED or CANCELLED");
    }
}
