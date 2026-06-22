package com.maersk.wms.task.domain.orchestration_service.event;

import com.maersk.wms.task.shared.kernel.events.TaskDomainEvent;
import com.maersk.wms.task.shared.kernel.identifiers.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Domain events for Task Orchestration bounded context.
 * Covers workflow coordination, dependencies, and multi-step execution.
 */
public final class OrchestrationEvents {

    private OrchestrationEvents() {}

    // ==================== Dependency Events ====================

    public record DependencyCreated(
            DependencyKey dependencyKey,
            TaskKey predecessorTaskKey,
            TaskKey successorTaskKey,
            String dependencyType,
            Instant createdAt,
            String createdBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return dependencyKey.value();
        }

        @Override
        public String eventType() {
            return "DEPENDENCY_CREATED";
        }

        @Override
        public Instant occurredAt() {
            return createdAt;
        }
    }

    public record DependencySatisfied(
            DependencyKey dependencyKey,
            TaskKey predecessorTaskKey,
            TaskKey successorTaskKey,
            Instant satisfiedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return dependencyKey.value();
        }

        @Override
        public String eventType() {
            return "DEPENDENCY_SATISFIED";
        }

        @Override
        public Instant occurredAt() {
            return satisfiedAt;
        }
    }

    public record DependencyViolated(
            DependencyKey dependencyKey,
            TaskKey violatingTaskKey,
            String violationType,
            Instant violatedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return dependencyKey.value();
        }

        @Override
        public String eventType() {
            return "DEPENDENCY_VIOLATED";
        }

        @Override
        public Instant occurredAt() {
            return violatedAt;
        }
    }

    public record DependencyRemoved(
            DependencyKey dependencyKey,
            String reason,
            Instant removedAt,
            String removedBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return dependencyKey.value();
        }

        @Override
        public String eventType() {
            return "DEPENDENCY_REMOVED";
        }

        @Override
        public Instant occurredAt() {
            return removedAt;
        }
    }

    // ==================== Workflow Events ====================

    public record WorkflowStarted(
            String workflowId,
            String workflowType,
            String sourceType,
            String sourceKey,
            List<TaskKey> initialTasks,
            Instant startedAt,
            String startedBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return workflowId;
        }

        @Override
        public String eventType() {
            return "WORKFLOW_STARTED";
        }

        @Override
        public Instant occurredAt() {
            return startedAt;
        }
    }

    public record WorkflowStepCompleted(
            String workflowId,
            int stepNumber,
            String stepName,
            TaskKey taskKey,
            Map<String, Object> stepResult,
            Instant completedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return workflowId;
        }

        @Override
        public String eventType() {
            return "WORKFLOW_STEP_COMPLETED";
        }

        @Override
        public Instant occurredAt() {
            return completedAt;
        }
    }

    public record WorkflowStepFailed(
            String workflowId,
            int stepNumber,
            String stepName,
            TaskKey taskKey,
            String errorCode,
            String errorMessage,
            Instant failedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return workflowId;
        }

        @Override
        public String eventType() {
            return "WORKFLOW_STEP_FAILED";
        }

        @Override
        public Instant occurredAt() {
            return failedAt;
        }
    }

    public record WorkflowCompleted(
            String workflowId,
            int totalSteps,
            int completedSteps,
            Map<String, Object> workflowResult,
            Instant completedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return workflowId;
        }

        @Override
        public String eventType() {
            return "WORKFLOW_COMPLETED";
        }

        @Override
        public Instant occurredAt() {
            return completedAt;
        }
    }

    public record WorkflowCancelled(
            String workflowId,
            String reason,
            int completedSteps,
            int remainingSteps,
            Instant cancelledAt,
            String cancelledBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return workflowId;
        }

        @Override
        public String eventType() {
            return "WORKFLOW_CANCELLED";
        }

        @Override
        public Instant occurredAt() {
            return cancelledAt;
        }
    }

    public record WorkflowRolledBack(
            String workflowId,
            int rollbackFromStep,
            String rollbackReason,
            Instant rolledBackAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return workflowId;
        }

        @Override
        public String eventType() {
            return "WORKFLOW_ROLLED_BACK";
        }

        @Override
        public Instant occurredAt() {
            return rolledBackAt;
        }
    }

    // ==================== Coordination Events ====================

    public record TasksCoordinated(
            String coordinationId,
            List<TaskKey> coordinatedTasks,
            String coordinationType,
            Instant coordinatedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return coordinationId;
        }

        @Override
        public String eventType() {
            return "TASKS_COORDINATED";
        }

        @Override
        public Instant occurredAt() {
            return coordinatedAt;
        }
    }

    public record TaskUnblocked(
            TaskKey taskKey,
            List<DependencyKey> satisfiedDependencies,
            Instant unblockedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_UNBLOCKED";
        }

        @Override
        public Instant occurredAt() {
            return unblockedAt;
        }
    }

    public record ParallelExecutionStarted(
            String executionId,
            List<TaskKey> parallelTasks,
            Instant startedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return executionId;
        }

        @Override
        public String eventType() {
            return "PARALLEL_EXECUTION_STARTED";
        }

        @Override
        public Instant occurredAt() {
            return startedAt;
        }
    }

    public record ParallelExecutionCompleted(
            String executionId,
            List<TaskKey> completedTasks,
            List<TaskKey> failedTasks,
            Instant completedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return executionId;
        }

        @Override
        public String eventType() {
            return "PARALLEL_EXECUTION_COMPLETED";
        }

        @Override
        public Instant occurredAt() {
            return completedAt;
        }
    }
}
