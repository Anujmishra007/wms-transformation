package com.maersk.wms.task.shared.kernel.exceptions;

/**
 * Exception thrown when a work queue is not found.
 */
public class WorkQueueNotFoundException extends TaskManagementException {

    public WorkQueueNotFoundException(String queueKey) {
        super("WORK_QUEUE_NOT_FOUND", "Work queue not found: " + queueKey);
    }
}
