package com.maersk.wms.task.domain.grouping_service.model;

/**
 * Enumeration of task group statuses.
 */
public enum TaskGroupStatus {
    CREATED(0, "Group created"),
    RELEASED(1, "Group released for execution"),
    ASSIGNED(2, "Group assigned to user"),
    IN_PROGRESS(3, "Group execution in progress"),
    SUSPENDED(4, "Group temporarily suspended"),
    COMPLETED(5, "All tasks in group completed"),
    CANCELLED(6, "Group cancelled"),
    CLOSED(9, "Group closed and archived");

    private final int statusCode;
    private final String description;

    TaskGroupStatus(int statusCode, String description) {
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
        return this == ASSIGNED || this == IN_PROGRESS;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED || this == CLOSED;
    }
}
