package com.maersk.wms.task.domain.lifecycle_service.model;

/**
 * Enumeration of task statuses.
 */
public enum TaskStatus {
    CREATED(0, "Task created but not released"),
    RELEASED(1, "Task released and available for assignment"),
    ASSIGNED(2, "Task assigned to user/device"),
    IN_PROGRESS(3, "Task execution in progress"),
    SUSPENDED(4, "Task temporarily suspended"),
    COMPLETED(5, "Task successfully completed"),
    CANCELLED(6, "Task cancelled"),
    CLOSED(9, "Task closed and archived");

    private final int statusCode;
    private final String description;

    TaskStatus(int statusCode, String description) {
        this.statusCode = statusCode;
        this.description = description;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == ASSIGNED || this == IN_PROGRESS || this == SUSPENDED;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED || this == CLOSED;
    }

    public boolean isPending() {
        return this == CREATED || this == RELEASED;
    }
}
