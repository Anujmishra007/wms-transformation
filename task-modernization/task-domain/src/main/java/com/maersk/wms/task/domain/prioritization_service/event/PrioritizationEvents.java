package com.maersk.wms.task.domain.prioritization_service.event;

import com.maersk.wms.task.shared.kernel.events.TaskDomainEvent;
import com.maersk.wms.task.shared.kernel.identifiers.*;
import com.maersk.wms.task.shared.kernel.valueobjects.TaskPriorityValue;
import com.maersk.wms.task.shared.kernel.valueobjects.WorkloadMetrics;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Domain events for Task Prioritization bounded context.
 * Covers dynamic priority calculation and workload balancing.
 */
public final class PrioritizationEvents {

    private PrioritizationEvents() {}

    // ==================== Priority Calculation Events ====================

    public record PriorityCalculated(
            TaskKey taskKey,
            TaskPriorityValue previousPriority,
            TaskPriorityValue newPriority,
            List<String> appliedRules,
            Instant calculatedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "PRIORITY_CALCULATED";
        }

        @Override
        public Instant occurredAt() {
            return calculatedAt;
        }
    }

    public record PriorityEscalated(
            TaskKey taskKey,
            TaskPriorityValue.PriorityLevel fromLevel,
            TaskPriorityValue.PriorityLevel toLevel,
            String escalationReason,
            Instant escalatedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "PRIORITY_ESCALATED";
        }

        @Override
        public Instant occurredAt() {
            return escalatedAt;
        }
    }

    public record PriorityDeescalated(
            TaskKey taskKey,
            TaskPriorityValue.PriorityLevel fromLevel,
            TaskPriorityValue.PriorityLevel toLevel,
            String reason,
            Instant deescalatedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "PRIORITY_DEESCALATED";
        }

        @Override
        public Instant occurredAt() {
            return deescalatedAt;
        }
    }

    public record BulkPriorityRecalculated(
            List<TaskKey> taskKeys,
            String recalculationReason,
            int tasksAffected,
            Instant recalculatedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return "BULK_" + recalculatedAt.toEpochMilli();
        }

        @Override
        public String eventType() {
            return "BULK_PRIORITY_RECALCULATED";
        }

        @Override
        public Instant occurredAt() {
            return recalculatedAt;
        }
    }

    // ==================== Priority Rule Events ====================

    public record PriorityRuleCreated(
            String ruleId,
            String ruleName,
            String ruleType,
            int ruleWeight,
            Map<String, Object> ruleConditions,
            Instant createdAt,
            String createdBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return ruleId;
        }

        @Override
        public String eventType() {
            return "PRIORITY_RULE_CREATED";
        }

        @Override
        public Instant occurredAt() {
            return createdAt;
        }
    }

    public record PriorityRuleUpdated(
            String ruleId,
            Map<String, Object> previousConditions,
            Map<String, Object> newConditions,
            Instant updatedAt,
            String updatedBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return ruleId;
        }

        @Override
        public String eventType() {
            return "PRIORITY_RULE_UPDATED";
        }

        @Override
        public Instant occurredAt() {
            return updatedAt;
        }
    }

    public record PriorityRuleActivated(
            String ruleId,
            Instant activatedAt,
            String activatedBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return ruleId;
        }

        @Override
        public String eventType() {
            return "PRIORITY_RULE_ACTIVATED";
        }

        @Override
        public Instant occurredAt() {
            return activatedAt;
        }
    }

    public record PriorityRuleDeactivated(
            String ruleId,
            String reason,
            Instant deactivatedAt,
            String deactivatedBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return ruleId;
        }

        @Override
        public String eventType() {
            return "PRIORITY_RULE_DEACTIVATED";
        }

        @Override
        public Instant occurredAt() {
            return deactivatedAt;
        }
    }

    public record PriorityRuleApplied(
            TaskKey taskKey,
            String ruleId,
            int priorityImpact,
            Instant appliedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "PRIORITY_RULE_APPLIED";
        }

        @Override
        public Instant occurredAt() {
            return appliedAt;
        }
    }

    // ==================== Workload Balancing Events ====================

    public record WorkloadCalculated(
            UserKey userId,
            WorkloadMetrics metrics,
            Instant calculatedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return userId.value();
        }

        @Override
        public String eventType() {
            return "WORKLOAD_CALCULATED";
        }

        @Override
        public Instant occurredAt() {
            return calculatedAt;
        }
    }

    public record WorkloadRebalanced(
            UserKey fromUserId,
            UserKey toUserId,
            List<TaskKey> transferredTasks,
            String rebalanceReason,
            Instant rebalancedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return fromUserId.value() + "_" + toUserId.value();
        }

        @Override
        public String eventType() {
            return "WORKLOAD_REBALANCED";
        }

        @Override
        public Instant occurredAt() {
            return rebalancedAt;
        }
    }

    public record WorkloadThresholdExceeded(
            UserKey userId,
            String thresholdType,
            double currentValue,
            double thresholdValue,
            Instant exceededAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return userId.value();
        }

        @Override
        public String eventType() {
            return "WORKLOAD_THRESHOLD_EXCEEDED";
        }

        @Override
        public Instant occurredAt() {
            return exceededAt;
        }
    }

    public record ZoneWorkloadUpdated(
            ZoneKey zoneKey,
            int activeTasks,
            int activeUsers,
            double averageTasksPerUser,
            Instant updatedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return zoneKey.value();
        }

        @Override
        public String eventType() {
            return "ZONE_WORKLOAD_UPDATED";
        }

        @Override
        public Instant occurredAt() {
            return updatedAt;
        }
    }

    // ==================== SLA Events ====================

    public record SlaAtRisk(
            TaskKey taskKey,
            Instant slaDeadline,
            long minutesRemaining,
            Instant detectedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "SLA_AT_RISK";
        }

        @Override
        public Instant occurredAt() {
            return detectedAt;
        }
    }

    public record SlaBreached(
            TaskKey taskKey,
            Instant slaDeadline,
            long minutesOverdue,
            Instant breachedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "SLA_BREACHED";
        }

        @Override
        public Instant occurredAt() {
            return breachedAt;
        }
    }
}
