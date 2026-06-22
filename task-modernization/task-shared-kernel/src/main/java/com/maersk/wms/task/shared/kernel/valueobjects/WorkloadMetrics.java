package com.maersk.wms.task.shared.kernel.valueobjects;

import java.time.Duration;

/**
 * Value object representing user workload metrics.
 */
public record WorkloadMetrics(
        int activeTasks,
        int pendingTasks,
        int completedToday,
        Duration avgTaskDuration,
        double utilizationPercent,
        int maxCapacity
) {

    public boolean hasCapacity() {
        return activeTasks < maxCapacity;
    }

    public int availableCapacity() {
        return Math.max(0, maxCapacity - activeTasks);
    }

    public boolean isOverloaded() {
        return utilizationPercent > 100;
    }

    public WorkloadMetrics incrementActive() {
        return new WorkloadMetrics(activeTasks + 1, pendingTasks, completedToday,
                avgTaskDuration, calculateUtilization(activeTasks + 1), maxCapacity);
    }

    public WorkloadMetrics decrementActive() {
        return new WorkloadMetrics(Math.max(0, activeTasks - 1), pendingTasks, completedToday + 1,
                avgTaskDuration, calculateUtilization(Math.max(0, activeTasks - 1)), maxCapacity);
    }

    private double calculateUtilization(int active) {
        return maxCapacity > 0 ? (active * 100.0 / maxCapacity) : 0;
    }
}
