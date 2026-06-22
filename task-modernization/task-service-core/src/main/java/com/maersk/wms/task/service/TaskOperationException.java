package com.maersk.wms.task.service;

/**
 * Exception thrown for task operation failures.
 */
public class TaskOperationException extends RuntimeException {

    private final String errorCode;

    public TaskOperationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public TaskOperationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
