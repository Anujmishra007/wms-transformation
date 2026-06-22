package com.maersk.wms.task.shared.kernel.events;

import java.time.Instant;

/**
 * Abstract base implementation for Task domain events.
 * Provides default implementations for common event fields.
 */
public interface AbstractTaskEvent extends TaskDomainEvent {

    @Override
    default Instant occurredAt() {
        return Instant.now();
    }
}
