package com.maersk.wms.task.plugin;

import com.maersk.wms.task.domain.entity.Task;
import com.maersk.wms.task.domain.enums.TaskPriority;

import java.util.List;

/**
 * Plugin interface for task prioritization customization.
 */
public interface TaskPrioritizationPlugin extends TaskPlugin {

    /**
     * Calculates priority for a task.
     */
    default TaskPriority calculatePriority(Task task, TaskPluginContext context) {
        return task.getPriority();
    }

    /**
     * Determines if task priority should be escalated.
     */
    default boolean shouldEscalate(Task task, TaskPluginContext context) {
        return false;
    }

    /**
     * Escalates task priority.
     */
    default TaskPriority escalatePriority(Task task, TaskPluginContext context) {
        return task.getPriority().escalate();
    }

    /**
     * Sorts tasks by priority (custom sorting logic).
     */
    default List<Task> sortByPriority(List<Task> tasks, TaskPluginContext context) {
        tasks.sort((t1, t2) -> {
            int priorityCompare = Integer.compare(t1.getPriority().getValue(), t2.getPriority().getValue());
            if (priorityCompare != 0) return priorityCompare;
            // Secondary sort by due date
            if (t1.getDueDate() != null && t2.getDueDate() != null) {
                return t1.getDueDate().compareTo(t2.getDueDate());
            }
            return 0;
        });
        return tasks;
    }

    /**
     * Calculates priority score for task ordering.
     */
    default int calculatePriorityScore(Task task, TaskPluginContext context) {
        int score = (10 - task.getPriority().getValue()) * 100;

        // Add urgency based on due date
        if (task.getDueDate() != null) {
            long hoursUntilDue = java.time.Duration.between(
                    java.time.LocalDateTime.now(), task.getDueDate()).toHours();
            if (hoursUntilDue < 1) score += 500;
            else if (hoursUntilDue < 4) score += 200;
            else if (hoursUntilDue < 8) score += 100;
        }

        return score;
    }

    /**
     * Determines the escalation threshold in minutes.
     */
    default int getEscalationThresholdMinutes(Task task, TaskPluginContext context) {
        return switch (task.getPriority()) {
            case CRITICAL -> 15;
            case URGENT -> 30;
            case HIGH -> 60;
            case NORMAL -> 120;
            case LOW -> 240;
            case DEFERRED -> 480;
        };
    }
}
