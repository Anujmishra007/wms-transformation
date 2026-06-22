package com.maersk.wms.task.shared.kernel.events;

import java.time.LocalDateTime;

/**
 * Abstract base implementation for Task domain events.
 */
public interface AbstractTaskEvent extends TaskDomainEvent {

    @Override
    default LocalDateTime timestamp() {
        return LocalDateTime.now();
    }
}
