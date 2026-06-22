package com.maersk.wms.task.domain.grouping_service.model;

import com.maersk.wms.task.shared.kernel.identifiers.*;
import com.maersk.wms.task.domain.lifecycle_service.model.TaskType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * WorkQueue aggregate root - represents a work queue for task distribution.
 * Part of Task Grouping bounded context.
 */
@Data
@Builder
public class WorkQueue {

    private WorkQueueKey queueKey;
    private String name;
    private String description;
    private WorkQueueStatus status;

    // Queue configuration
    private ZoneKey zone;
    private TaskType taskType; // Optional: restrict to specific task type
    private int maxCapacity;
    private int currentCount;
    private QueueStrategy strategy;

    // Priority configuration
    private int basePriority;
    private boolean enablePriorityEscalation;
    private int escalationThresholdMinutes;

    // Assignment configuration
    @Builder.Default
    private List<UserKey> authorizedUsers = new ArrayList<>();
    @Builder.Default
    private List<String> authorizedWorkGroups = new ArrayList<>();
    private boolean autoAssign;
    private int maxTasksPerUser;

    // Tasks in queue
    @Builder.Default
    private List<TaskKey> pendingTasks = new ArrayList<>();
    private int processedToday;

    // Timing
    private LocalDateTime createdAt;
    private LocalDateTime lastActivityAt;

    public enum WorkQueueStatus {
        ACTIVE, PAUSED, CLOSED
    }

    public enum QueueStrategy {
        FIFO, // First In First Out
        PRIORITY, // Priority-based
        NEAREST, // Nearest location first
        SLA_BASED, // SLA deadline based
        BALANCED // Balanced distribution
    }

    public void addTask(TaskKey taskKey) {
        if (!pendingTasks.contains(taskKey) && currentCount < maxCapacity) {
            pendingTasks.add(taskKey);
            currentCount++;
            lastActivityAt = LocalDateTime.now();
        }
    }

    public TaskKey getNextTask() {
        if (pendingTasks.isEmpty()) {
            return null;
        }
        TaskKey next = pendingTasks.remove(0);
        currentCount--;
        processedToday++;
        lastActivityAt = LocalDateTime.now();
        return next;
    }

    public void removeTask(TaskKey taskKey) {
        if (pendingTasks.remove(taskKey)) {
            currentCount--;
            lastActivityAt = LocalDateTime.now();
        }
    }

    public boolean hasCapacity() {
        return currentCount < maxCapacity;
    }

    public int getAvailableCapacity() {
        return maxCapacity - currentCount;
    }

    public boolean isUserAuthorized(UserKey userId) {
        return authorizedUsers.isEmpty() || authorizedUsers.contains(userId);
    }

    public void pause() {
        this.status = WorkQueueStatus.PAUSED;
    }

    public void resume() {
        this.status = WorkQueueStatus.ACTIVE;
    }

    public void close() {
        this.status = WorkQueueStatus.CLOSED;
    }
}
