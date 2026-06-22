package com.maersk.wms.task.domain.lifecycle_service.model;

import com.maersk.wms.task.shared.kernel.identifiers.*;
import com.maersk.wms.task.shared.kernel.valueobjects.*;
import com.maersk.wms.task.shared.kernel.exceptions.TaskStateException;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Task aggregate root - represents a warehouse task.
 * Part of Task Lifecycle bounded context.
 */
@Data
@Builder
public class Task {

    private TaskKey taskKey;
    private TaskType taskType;
    private TaskStatus status;
    private TaskPriorityValue priority;

    // Source reference
    private String sourceType; // PICK, PUTAWAY, COUNT, REPLENISHMENT, etc.
    private String sourceKey;
    private TaskGroupKey groupKey;
    private WorkQueueKey queueKey;

    // Location details
    private LocationKey fromLocation;
    private LocationKey toLocation;
    private ZoneKey zone;
    private String aisle;
    private String bay;

    // Item details
    private LpnKey lpn;
    private SkuKey sku;
    private BigDecimal quantity;
    private String uom;

    // Context
    private TaskContext context;

    // Assignment
    private UserKey assignedUser;
    private DeviceKey assignedDevice;
    private LocalDateTime assignedTime;

    // Timing
    private Duration estimatedDuration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime dueTime;

    // Instructions
    private String instructions;
    private String specialHandling;

    // Audit
    private UserKey createdBy;
    private LocalDateTime createdAt;
    private String editWho;
    private LocalDateTime editDate;

    // Status transitions
    public boolean canAssign() {
        return status == TaskStatus.CREATED || status == TaskStatus.RELEASED;
    }

    public boolean canStart() {
        return status == TaskStatus.ASSIGNED;
    }

    public boolean canComplete() {
        return status == TaskStatus.IN_PROGRESS;
    }

    public boolean canCancel() {
        return status != TaskStatus.COMPLETED && status != TaskStatus.CANCELLED && status != TaskStatus.CLOSED;
    }

    public boolean canSuspend() {
        return status == TaskStatus.IN_PROGRESS;
    }

    public void release() {
        if (status != TaskStatus.CREATED) {
            throw new TaskStateException(status.name(), TaskStatus.RELEASED.name());
        }
        this.status = TaskStatus.RELEASED;
    }

    public void assign(UserKey user, DeviceKey device) {
        if (!canAssign()) {
            throw new TaskStateException(status.name(), TaskStatus.ASSIGNED.name());
        }
        this.assignedUser = user;
        this.assignedDevice = device;
        this.assignedTime = LocalDateTime.now();
        this.status = TaskStatus.ASSIGNED;
    }

    public void unassign() {
        this.assignedUser = null;
        this.assignedDevice = null;
        this.assignedTime = null;
        this.status = TaskStatus.RELEASED;
    }

    public void start() {
        if (!canStart()) {
            throw new TaskStateException(status.name(), TaskStatus.IN_PROGRESS.name());
        }
        this.startTime = LocalDateTime.now();
        this.status = TaskStatus.IN_PROGRESS;
    }

    public void suspend(String reason) {
        if (!canSuspend()) {
            throw new TaskStateException(status.name(), TaskStatus.SUSPENDED.name());
        }
        this.status = TaskStatus.SUSPENDED;
    }

    public void resume() {
        if (status != TaskStatus.SUSPENDED) {
            throw new TaskStateException(status.name(), TaskStatus.IN_PROGRESS.name());
        }
        this.status = TaskStatus.IN_PROGRESS;
    }

    public void complete(BigDecimal actualQty) {
        if (!canComplete()) {
            throw new TaskStateException(status.name(), TaskStatus.COMPLETED.name());
        }
        this.quantity = actualQty;
        this.endTime = LocalDateTime.now();
        this.status = TaskStatus.COMPLETED;
    }

    public void cancel(String reason) {
        if (!canCancel()) {
            throw new TaskStateException(status.name(), TaskStatus.CANCELLED.name());
        }
        this.status = TaskStatus.CANCELLED;
        this.endTime = LocalDateTime.now();
    }

    public void close() {
        if (status != TaskStatus.COMPLETED && status != TaskStatus.CANCELLED) {
            throw new TaskStateException(status.name(), TaskStatus.CLOSED.name());
        }
        this.status = TaskStatus.CLOSED;
    }

    public boolean isOverdue() {
        return dueTime != null && LocalDateTime.now().isAfter(dueTime) && status != TaskStatus.COMPLETED;
    }

    public Duration getActualDuration() {
        if (startTime == null || endTime == null) {
            return null;
        }
        return Duration.between(startTime, endTime);
    }
}
