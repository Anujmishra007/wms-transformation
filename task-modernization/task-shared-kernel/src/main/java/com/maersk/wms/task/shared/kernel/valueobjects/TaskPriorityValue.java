package com.maersk.wms.task.shared.kernel.valueobjects;

/**
 * Value object representing task priority with numeric score.
 */
public record TaskPriorityValue(
        int score,
        PriorityLevel level,
        String reason
) {

    public TaskPriorityValue {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Priority score must be between 0 and 100");
        }
    }

    public static TaskPriorityValue high(String reason) {
        return new TaskPriorityValue(90, PriorityLevel.HIGH, reason);
    }

    public static TaskPriorityValue medium(String reason) {
        return new TaskPriorityValue(50, PriorityLevel.MEDIUM, reason);
    }

    public static TaskPriorityValue low(String reason) {
        return new TaskPriorityValue(20, PriorityLevel.LOW, reason);
    }

    public static TaskPriorityValue critical(String reason) {
        return new TaskPriorityValue(100, PriorityLevel.CRITICAL, reason);
    }

    public boolean isHigherThan(TaskPriorityValue other) {
        return this.score > other.score;
    }

    public enum PriorityLevel {
        CRITICAL, HIGH, MEDIUM, LOW, NORMAL
    }
}
