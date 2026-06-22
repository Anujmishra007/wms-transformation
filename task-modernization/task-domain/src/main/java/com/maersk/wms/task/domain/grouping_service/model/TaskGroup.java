package com.maersk.wms.task.domain.grouping_service.model;

import com.maersk.wms.task.shared.kernel.identifiers.*;
import com.maersk.wms.task.shared.kernel.valueobjects.TaskPriorityValue;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * TaskGroup aggregate root - represents a group of related tasks.
 * Part of Task Grouping bounded context.
 */
@Data
@Builder
public class TaskGroup {

    private TaskGroupKey groupKey;
    private TaskGroupType groupType;
    private TaskGroupStatus status;
    private String name;
    private String description;

    // Parent grouping
    private WaveKey waveKey;
    private String batchId;
    private String routeId;
    private ZoneKey zone;

    // Tasks in this group
    @Builder.Default
    private List<TaskKey> taskKeys = new ArrayList<>();
    private int totalTasks;
    private int completedTasks;
    private int cancelledTasks;

    // Priority
    private TaskPriorityValue priority;
    private LocalDateTime dueTime;

    // Assignment
    private UserKey assignedUser;
    private DeviceKey assignedDevice;
    private boolean singleUserGroup; // All tasks must be done by same user

    // Progress
    private double completionPercent;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Audit
    private UserKey createdBy;
    private LocalDateTime createdAt;
    private String editWho;
    private LocalDateTime editDate;

    public void addTask(TaskKey taskKey) {
        if (!taskKeys.contains(taskKey)) {
            taskKeys.add(taskKey);
            totalTasks++;
            updateCompletionPercent();
        }
    }

    public void removeTask(TaskKey taskKey) {
        if (taskKeys.remove(taskKey)) {
            totalTasks--;
            updateCompletionPercent();
        }
    }

    public void markTaskCompleted(TaskKey taskKey) {
        if (taskKeys.contains(taskKey)) {
            completedTasks++;
            updateCompletionPercent();
            if (isComplete()) {
                this.status = TaskGroupStatus.COMPLETED;
                this.endTime = LocalDateTime.now();
            }
        }
    }

    public void markTaskCancelled(TaskKey taskKey) {
        if (taskKeys.contains(taskKey)) {
            cancelledTasks++;
            updateCompletionPercent();
        }
    }

    private void updateCompletionPercent() {
        if (totalTasks > 0) {
            completionPercent = ((double) completedTasks / totalTasks) * 100;
        }
    }

    public boolean isComplete() {
        return completedTasks + cancelledTasks >= totalTasks && totalTasks > 0;
    }

    public void release() {
        this.status = TaskGroupStatus.RELEASED;
    }

    public void start() {
        this.status = TaskGroupStatus.IN_PROGRESS;
        this.startTime = LocalDateTime.now();
    }

    public void suspend() {
        this.status = TaskGroupStatus.SUSPENDED;
    }

    public void cancel(String reason) {
        this.status = TaskGroupStatus.CANCELLED;
        this.endTime = LocalDateTime.now();
    }

    public int getPendingTasks() {
        return totalTasks - completedTasks - cancelledTasks;
    }
}
