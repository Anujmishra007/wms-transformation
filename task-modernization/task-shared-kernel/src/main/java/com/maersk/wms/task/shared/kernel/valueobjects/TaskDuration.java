package com.maersk.wms.task.shared.kernel.valueobjects;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Value object representing estimated and actual task duration.
 */
public record TaskDuration(
        Duration estimatedDuration,
        Duration actualDuration,
        LocalDateTime startTime,
        LocalDateTime endTime
) {

    public static TaskDuration estimated(Duration duration) {
        return new TaskDuration(duration, null, null, null);
    }

    public TaskDuration start(LocalDateTime time) {
        return new TaskDuration(estimatedDuration, null, time, null);
    }

    public TaskDuration complete(LocalDateTime time) {
        Duration actual = startTime != null ? Duration.between(startTime, time) : null;
        return new TaskDuration(estimatedDuration, actual, startTime, time);
    }

    public boolean isOverdue() {
        if (startTime == null || estimatedDuration == null) {
            return false;
        }
        LocalDateTime expectedEnd = startTime.plus(estimatedDuration);
        return LocalDateTime.now().isAfter(expectedEnd) && endTime == null;
    }

    public Duration getVariance() {
        if (actualDuration == null || estimatedDuration == null) {
            return null;
        }
        return actualDuration.minus(estimatedDuration);
    }
}
