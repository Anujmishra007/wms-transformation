package com.maersk.wms.task.domain.orchestration_service.model;

import com.maersk.wms.task.shared.kernel.identifiers.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TaskDependency entity - defines dependencies between tasks.
 * Part of Task Orchestration bounded context.
 */
@Data
@Builder
public class TaskDependency {

    private DependencyKey dependencyKey;
    private TaskKey predecessorTask;
    private TaskKey successorTask;
    private DependencyType type;
    private DependencyStatus status;

    // Timing constraints
    private int lagMinutes; // Minimum delay between predecessor end and successor start
    private int leadMinutes; // How many minutes before predecessor completes can successor start

    // Resolution
    private LocalDateTime resolvedAt;
    private String resolvedBy;

    // Audit
    private LocalDateTime createdAt;

    public enum DependencyType {
        FINISH_TO_START, // Successor starts after predecessor finishes
        START_TO_START, // Successor starts when predecessor starts
        FINISH_TO_FINISH, // Successor finishes when predecessor finishes
        START_TO_FINISH, // Successor finishes when predecessor starts
        MANDATORY, // Strict dependency
        OPTIONAL // Soft dependency - can be overridden
    }

    public enum DependencyStatus {
        PENDING, // Waiting for predecessor
        RESOLVED, // Dependency met, successor can proceed
        BLOCKED, // Predecessor failed/cancelled
        OVERRIDDEN // Manually overridden
    }

    public boolean isResolved() {
        return status == DependencyStatus.RESOLVED || status == DependencyStatus.OVERRIDDEN;
    }

    public boolean isBlocking() {
        return status == DependencyStatus.PENDING || status == DependencyStatus.BLOCKED;
    }

    public void resolve() {
        this.status = DependencyStatus.RESOLVED;
        this.resolvedAt = LocalDateTime.now();
        this.resolvedBy = "SYSTEM";
    }

    public void block() {
        this.status = DependencyStatus.BLOCKED;
    }

    public void override(String overriddenBy) {
        this.status = DependencyStatus.OVERRIDDEN;
        this.resolvedAt = LocalDateTime.now();
        this.resolvedBy = overriddenBy;
    }

    public boolean canSuccessorStart(LocalDateTime predecessorEndTime) {
        if (!isResolved()) {
            return false;
        }
        if (lagMinutes > 0 && predecessorEndTime != null) {
            return LocalDateTime.now().isAfter(predecessorEndTime.plusMinutes(lagMinutes));
        }
        return true;
    }
}
