package com.maersk.wms.picking.shared.kernel.events;

/**
 * Enumeration of bounded contexts in the Picking domain.
 * Used for event routing and service identification.
 */
public enum PickingBoundedContext {
    TASK_EXECUTION("picking-task-execution-service", "Task Execution"),
    PROGRESSION("picking-progression-service", "Pick Progression"),
    SHORTS("picking-shorts-service", "Shorts Handling"),
    CANCELLATION("picking-cancellation-service", "Cancellation Handling"),
    LIST_MANAGEMENT("picking-list-management-service", "List Management");

    private final String serviceId;
    private final String description;

    PickingBoundedContext(String serviceId, String description) {
        this.serviceId = serviceId;
        this.description = description;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getDescription() {
        return description;
    }
}
