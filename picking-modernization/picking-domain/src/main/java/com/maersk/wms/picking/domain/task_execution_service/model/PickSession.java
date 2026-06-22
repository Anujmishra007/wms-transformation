package com.maersk.wms.picking.domain.task_execution_service.model;

import com.maersk.wms.picking.shared.kernel.identifiers.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * PickSession - represents an active picking session for a user/device.
 * Part of Task Execution Service bounded context.
 */
@Data
@Builder
public class PickSession {

    private String sessionId;
    private UserKey userId;
    private DeviceKey deviceId;
    private PickListKey pickListKey;

    // Session attributes
    private SessionStatus status;
    private String zone;
    private String equipmentType;

    // Current task
    private PickTaskKey currentTaskKey;
    private int currentSequence;

    // Progress tracking
    @Builder.Default
    private List<PickTaskKey> completedTasks = new ArrayList<>();
    @Builder.Default
    private List<PickTaskKey> skippedTasks = new ArrayList<>();
    private int totalTasks;

    // Timing
    private LocalDateTime startTime;
    private LocalDateTime lastActivityTime;
    private LocalDateTime endTime;

    // Metrics
    private int tasksCompleted;
    private int tasksShorted;
    private long totalPickTimeMs;
    private String pauseReason;

    // Business methods
    public boolean isActive() {
        return status == SessionStatus.ACTIVE;
    }

    public void end() {
        this.status = SessionStatus.ENDED;
        this.endTime = LocalDateTime.now();
    }

    public void pause(String reason) {
        this.status = SessionStatus.PAUSED;
        this.pauseReason = reason;
        recordActivity();
    }

    public void resume() {
        this.status = SessionStatus.ACTIVE;
        this.pauseReason = null;
        recordActivity();
    }

    public int getRemainingTasks() {
        return totalTasks - completedTasks.size() - skippedTasks.size();
    }

    public double getCompletionPercentage() {
        if (totalTasks == 0) return 0.0;
        return (double) completedTasks.size() / totalTasks * 100.0;
    }

    public double getPicksPerMinute() {
        if (totalPickTimeMs == 0) return 0.0;
        double minutes = totalPickTimeMs / 60000.0;
        return picksCompleted / minutes;
    }

    public void recordActivity() {
        this.lastActivityTime = LocalDateTime.now();
    }

    public void completeTask(PickTaskKey taskKey, long pickTimeMs) {
        completedTasks.add(taskKey);
        picksCompleted++;
        totalPickTimeMs += pickTimeMs;
        recordActivity();
    }

    public void skipTask(PickTaskKey taskKey) {
        skippedTasks.add(taskKey);
        recordActivity();
    }

    public void recordShort() {
        shortsRecorded++;
        recordActivity();
    }
}
