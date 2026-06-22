package com.maersk.wms.task.domain.prioritization_service.model;

import com.maersk.wms.task.shared.kernel.identifiers.*;
import com.maersk.wms.task.shared.kernel.valueobjects.WorkloadMetrics;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * UserWorkload entity - tracks user workload for task distribution.
 * Part of Task Prioritization bounded context.
 */
@Data
@Builder
public class UserWorkload {

    private UserKey userId;
    private DeviceKey currentDevice;
    private WorkloadStatus status;

    // Current assignment
    @Builder.Default
    private List<TaskKey> activeTasks = new ArrayList<>();
    @Builder.Default
    private List<TaskKey> pendingTasks = new ArrayList<>();
    private int maxConcurrentTasks;

    // Metrics
    private WorkloadMetrics metrics;
    private Duration avgTaskDuration;
    private int tasksCompletedToday;
    private double productivityScore;

    // Zone assignment
    @Builder.Default
    private List<ZoneKey> assignedZones = new ArrayList<>();
    private ZoneKey currentZone;

    // Shift information
    private LocalDateTime shiftStart;
    private LocalDateTime shiftEnd;
    private LocalDateTime lastActivityTime;

    // Status
    private LocalDateTime statusChangedAt;

    public enum WorkloadStatus {
        AVAILABLE, BUSY, ON_BREAK, OFFLINE
    }

    public boolean canAcceptTask() {
        return status == WorkloadStatus.AVAILABLE &&
               activeTasks.size() < maxConcurrentTasks &&
               isWithinShift();
    }

    public boolean isWithinShift() {
        LocalDateTime now = LocalDateTime.now();
        if (shiftStart != null && now.isBefore(shiftStart)) return false;
        if (shiftEnd != null && now.isAfter(shiftEnd)) return false;
        return true;
    }

    public void assignTask(TaskKey taskKey) {
        if (!activeTasks.contains(taskKey)) {
            activeTasks.add(taskKey);
            lastActivityTime = LocalDateTime.now();
            if (activeTasks.size() >= maxConcurrentTasks) {
                status = WorkloadStatus.BUSY;
                statusChangedAt = LocalDateTime.now();
            }
        }
    }

    public void completeTask(TaskKey taskKey) {
        activeTasks.remove(taskKey);
        tasksCompletedToday++;
        lastActivityTime = LocalDateTime.now();
        if (status == WorkloadStatus.BUSY && activeTasks.size() < maxConcurrentTasks) {
            status = WorkloadStatus.AVAILABLE;
            statusChangedAt = LocalDateTime.now();
        }
    }

    public void removeTask(TaskKey taskKey) {
        activeTasks.remove(taskKey);
        pendingTasks.remove(taskKey);
        if (status == WorkloadStatus.BUSY && activeTasks.size() < maxConcurrentTasks) {
            status = WorkloadStatus.AVAILABLE;
            statusChangedAt = LocalDateTime.now();
        }
    }

    public void goOnBreak() {
        status = WorkloadStatus.ON_BREAK;
        statusChangedAt = LocalDateTime.now();
    }

    public void returnFromBreak() {
        status = activeTasks.size() < maxConcurrentTasks ? WorkloadStatus.AVAILABLE : WorkloadStatus.BUSY;
        statusChangedAt = LocalDateTime.now();
    }

    public void goOffline() {
        status = WorkloadStatus.OFFLINE;
        statusChangedAt = LocalDateTime.now();
    }

    public int getAvailableCapacity() {
        return maxConcurrentTasks - activeTasks.size();
    }

    public boolean isInZone(ZoneKey zone) {
        return assignedZones.isEmpty() || assignedZones.contains(zone);
    }
}
