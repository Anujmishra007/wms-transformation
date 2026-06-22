package com.maersk.wms.picking.domain.list_management_service.model;

import com.maersk.wms.picking.shared.kernel.identifiers.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * PickList aggregate - groups pick tasks for assignment and execution.
 * Part of List Management Service bounded context.
 */
@Data
@Builder
public class PickList {

    private PickListKey pickListKey;
    private WaveKey waveKey;

    // List attributes
    private PickListStatus status;
    private PickListType type;
    private String description;

    // Zone assignment
    private String zone;
    private String aisle;
    private String equipmentType;

    // User assignment
    private UserKey assignedUser;
    private DeviceKey assignedDevice;
    private LocalDateTime assignedTime;

    // Tasks
    @Builder.Default
    private List<PickTaskKey> pickTasks = new ArrayList<>();

    // Progress
    private int totalTasks;
    private int completedTasks;
    private int shortedTasks;
    private int skippedTasks;
    private int cancelledTasks;

    // Routing
    private String route;
    private int estimatedMinutes;
    private double totalDistance;

    // Timing
    private LocalDateTime createdTime;
    private LocalDateTime releasedTime;
    private LocalDateTime startTime;
    private LocalDateTime completeTime;

    // Audit
    private String addWho;
    private LocalDateTime addDate;
    private String editWho;
    private LocalDateTime editDate;

    // Business methods
    public boolean isComplete() {
        return completedTasks + shortedTasks + skippedTasks + cancelledTasks >= totalTasks;
    }

    public int getRemainingTasks() {
        return totalTasks - completedTasks - shortedTasks - skippedTasks - cancelledTasks;
    }

    public double getCompletionPercentage() {
        if (totalTasks == 0) return 0.0;
        return (double) (completedTasks + shortedTasks) / totalTasks * 100.0;
    }

    public boolean canAssign() {
        return status == PickListStatus.RELEASED && assignedUser == null;
    }

    public boolean canStart() {
        return status == PickListStatus.ASSIGNED && startTime == null;
    }

    public void addTask(PickTaskKey taskKey) {
        if (!pickTasks.contains(taskKey)) {
            pickTasks.add(taskKey);
            totalTasks++;
        }
    }

    public void removeTask(PickTaskKey taskKey) {
        if (pickTasks.remove(taskKey)) {
            totalTasks--;
        }
    }

    public void assign(UserKey userId, DeviceKey deviceId) {
        this.assignedUser = userId;
        this.assignedDevice = deviceId;
        this.assignedTime = LocalDateTime.now();
        this.status = PickListStatus.ASSIGNED;
    }

    public void start() {
        this.startTime = LocalDateTime.now();
        this.status = PickListStatus.IN_PROGRESS;
    }

    public void complete() {
        this.completeTime = LocalDateTime.now();
        this.status = PickListStatus.COMPLETED;
    }
}
