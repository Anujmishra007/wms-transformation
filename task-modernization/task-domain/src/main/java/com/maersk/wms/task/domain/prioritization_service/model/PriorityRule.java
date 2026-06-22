package com.maersk.wms.task.domain.prioritization_service.model;

import com.maersk.wms.task.domain.lifecycle_service.model.TaskType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * PriorityRule entity - defines task prioritization rules.
 * Part of Task Prioritization bounded context.
 */
@Data
@Builder
public class PriorityRule {

    private String ruleKey;
    private String name;
    private String description;
    private RuleType ruleType;
    private boolean active;
    private int precedence; // Lower number = higher precedence

    // Criteria
    private TaskType taskType; // Optional: applies to specific task type
    private String customerId; // Optional: applies to specific customer
    private String zone; // Optional: applies to specific zone
    private String carrier; // Optional: applies to specific carrier

    // Priority adjustment
    private int priorityBoost; // Points to add to base priority
    private boolean escalateOnDelay;
    private int escalationMinutes;
    private int escalationBoost;

    // Time-based rules
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;
    private String timeWindow; // e.g., "08:00-12:00"

    // Conditions
    private String conditionExpression; // SpEL or rule expression
    private int thresholdValue; // For threshold-based rules

    // Audit
    private LocalDateTime createdAt;
    private String createdBy;

    public enum RuleType {
        SLA_BASED, // Priority based on SLA deadline
        CUSTOMER_TIER, // Priority based on customer tier
        CARRIER_CUTOFF, // Priority based on carrier cutoff time
        INVENTORY_LEVEL, // Priority based on inventory levels
        WAVE_DEADLINE, // Priority based on wave deadline
        ORDER_VALUE, // Priority based on order value
        AGING, // Priority based on task age
        CUSTOM // Custom rule expression
    }

    public boolean isEffective() {
        if (!active) return false;
        LocalDateTime now = LocalDateTime.now();
        if (effectiveFrom != null && now.isBefore(effectiveFrom)) return false;
        if (effectiveTo != null && now.isAfter(effectiveTo)) return false;
        return true;
    }

    public boolean appliesTo(TaskType type, String customer, String taskZone) {
        if (taskType != null && taskType != type) return false;
        if (customerId != null && !customerId.equals(customer)) return false;
        if (zone != null && !zone.equals(taskZone)) return false;
        return true;
    }

    public int calculateAdjustment(int baseScore, int taskAgeMinutes) {
        int adjustment = priorityBoost;

        if (escalateOnDelay && taskAgeMinutes > escalationMinutes) {
            int escalations = (taskAgeMinutes - escalationMinutes) / escalationMinutes;
            adjustment += escalations * escalationBoost;
        }

        return Math.min(100, baseScore + adjustment);
    }
}
