package com.maersk.wms.task.shared.kernel.events;

import java.time.LocalDateTime;

/**
 * Base interface for all Task Management domain events.
 */
public interface TaskDomainEvent {

    String eventId();
    LocalDateTime timestamp();
    TaskBoundedContext boundedContext();
    String aggregateId();

    default String eventType() {
        return getClass().getSimpleName();
    }

    default String topic() {
        return boundedContext().getEventTopic(eventType());
    }
}
