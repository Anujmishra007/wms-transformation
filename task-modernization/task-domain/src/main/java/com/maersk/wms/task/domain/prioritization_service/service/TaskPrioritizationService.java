package com.maersk.wms.task.domain.prioritization_service.service;

import com.maersk.wms.task.domain.prioritization_service.model.*;
import com.maersk.wms.task.domain.lifecycle_service.model.TaskType;
import com.maersk.wms.task.shared.kernel.identifiers.*;
import com.maersk.wms.task.shared.kernel.valueobjects.TaskPriorityValue;

import java.util.List;
import java.util.Optional;

/**
 * Task Prioritization Service - manages task priority and workload distribution.
 */
public interface TaskPrioritizationService {

    // Priority Calculation
    TaskPriorityValue calculatePriority(TaskKey taskKey);
    TaskPriorityValue calculatePriority(TaskType type, String customerId, ZoneKey zone,
                                         int taskAgeMinutes, boolean hasSla);
    void updateTaskPriority(TaskKey taskKey, TaskPriorityValue priority);
    void escalatePriority(TaskKey taskKey, String reason);
    void escalateOverdueTasks();

    // Priority Rules
    PriorityRule createRule(String name, PriorityRule.RuleType type, int priorityBoost, int precedence);
    PriorityRule getRule(String ruleKey);
    List<PriorityRule> getActiveRules();
    List<PriorityRule> getRulesForTaskType(TaskType taskType);

    void activateRule(String ruleKey);
    void deactivateRule(String ruleKey);
    void updateRule(String ruleKey, PriorityRule updates);
    void deleteRule(String ruleKey);

    // Workload Management
    UserWorkload getUserWorkload(UserKey userId);
    List<UserWorkload> getAvailableUsers(ZoneKey zone);
    List<UserWorkload> getUsersByStatus(UserWorkload.WorkloadStatus status);

    void updateUserWorkload(UserKey userId, UserWorkload.WorkloadStatus status);
    void assignTaskToUser(UserKey userId, TaskKey taskKey);
    void completeTaskForUser(UserKey userId, TaskKey taskKey);
    void removeTaskFromUser(UserKey userId, TaskKey taskKey);

    // Load Balancing
    Optional<UserKey> findBestUserForTask(TaskKey taskKey, ZoneKey zone);
    Optional<UserKey> findBestUserForTask(TaskType type, ZoneKey zone);
    List<UserKey> rankUsersForTask(TaskKey taskKey, List<UserKey> candidates);
    void rebalanceWorkload(ZoneKey zone);

    // User Zone Assignment
    void assignUserToZone(UserKey userId, ZoneKey zone);
    void removeUserFromZone(UserKey userId, ZoneKey zone);
    List<ZoneKey> getUserZones(UserKey userId);
    List<UserKey> getUsersInZone(ZoneKey zone);

    // Metrics
    double getZoneUtilization(ZoneKey zone);
    double getUserUtilization(UserKey userId);
    int getPendingTaskCount(ZoneKey zone);
}
