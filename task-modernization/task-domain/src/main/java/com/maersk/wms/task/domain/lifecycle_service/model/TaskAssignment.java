package com.maersk.wms.task.domain.lifecycle_service.model;

import com.maersk.wms.task.shared.kernel.identifiers.*;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * TaskAssignment entity - represents user assignment to a task.
 */
@Data
@Builder
public class TaskAssignment {

    private AssignmentKey assignmentKey;
    private TaskKey taskKey;
    private UserKey userId;
    private DeviceKey deviceId;
    private AssignmentStatus status;
    private AssignmentType type;

    // Timing
    private LocalDateTime assignedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Duration duration;

    // Assignment details
    private int sequence; // Order in user's task list
    private boolean autoAssigned;
    private String assignedBy; // System or user
    private String unassignReason;

    // Audit
    private String addWho;
    private LocalDateTime addDate;

    public enum AssignmentStatus {
        PENDING, ACTIVE, COMPLETED, CANCELLED, REASSIGNED
    }

    public enum AssignmentType {
        MANUAL, AUTO_DIRECTED, QUEUE_BASED, WAVE_BASED, BATCH_BASED
    }

    public void start() {
        this.startedAt = LocalDateTime.now();
        this.status = AssignmentStatus.ACTIVE;
    }

    public void complete() {
        this.completedAt = LocalDateTime.now();
        this.duration = Duration.between(startedAt, completedAt);
        this.status = AssignmentStatus.COMPLETED;
    }

    public void cancel(String reason) {
        this.unassignReason = reason;
        this.status = AssignmentStatus.CANCELLED;
    }

    public void reassign(UserKey newUser) {
        this.status = AssignmentStatus.REASSIGNED;
    }

    public boolean isActive() {
        return status == AssignmentStatus.ACTIVE;
    }
}
