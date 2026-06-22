package com.maersk.wms.task.shared.kernel.exceptions;

/**
 * Base exception for Task Management domain.
 */
public class TaskManagementException extends RuntimeException {

    private final String errorCode;

    public TaskManagementException(String message) {
        super(message);
        this.errorCode = "TASK_ERROR";
    }

    public TaskManagementException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public TaskManagementException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "TASK_ERROR";
    }

    public String getErrorCode() {
        return errorCode;
    }
}
