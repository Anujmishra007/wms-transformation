package com.maersk.wms.task.shared.kernel.exceptions;

/**
 * Exception thrown when work queue operations fail.
 */
public class WorkQueueException extends TaskManagementException {

    public WorkQueueException(String message) {
        super("WORK_QUEUE_ERROR", message);
    }
}
