package com.maersk.wms.task.shared.kernel.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Base interface for all Task Management domain events.
 */
public interface TaskDomainEvent {

    String aggregateId();
    Instant occurredAt();

    default String eventId() {
        return UUID.randomUUID().toString();
    }

    default String eventType() {
        return getClass().getSimpleName();
    }

    default TaskBoundedContext boundedContext() {
        return TaskBoundedContext.ORCHESTRATION;
    }

    default String topic() {
        return boundedContext().getEventTopic(eventType());
    }
}
