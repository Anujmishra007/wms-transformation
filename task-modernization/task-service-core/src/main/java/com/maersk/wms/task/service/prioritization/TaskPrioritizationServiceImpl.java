package com.maersk.wms.task.service.prioritization;

import com.maersk.wms.task.domain.prioritization_service.model.*;
import com.maersk.wms.task.domain.prioritization_service.repository.*;
import com.maersk.wms.task.domain.prioritization_service.service.TaskPrioritizationService;
import com.maersk.wms.task.domain.prioritization_service.event.PrioritizationEvents;
import com.maersk.wms.task.domain.lifecycle_service.model.Task;
import com.maersk.wms.task.domain.lifecycle_service.model.TaskType;
import com.maersk.wms.task.domain.lifecycle_service.repository.TaskRepository;
import com.maersk.wms.task.shared.kernel.identifiers.*;
import com.maersk.wms.task.shared.kernel.valueobjects.TaskPriorityValue;
import com.maersk.wms.task.shared.kernel.valueobjects.WorkloadMetrics;
import com.maersk.wms.task.shared.kernel.exceptions.TaskNotFoundException;
import com.maersk.wms.task.shared.kernel.exceptions.PriorityRuleNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of Task Prioritization Service.
 * Manages dynamic priority calculation and workload distribution.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TaskPrioritizationServiceImpl implements TaskPrioritizationService {

    private final TaskRepository taskRepository;
    private final PriorityRuleRepository ruleRepository;
    private final UserWorkloadRepository workloadRepository;
    private final ApplicationEventPublisher eventPublisher;

    private static final int BASE_PRIORITY = 50;
    private static final int SLA_BOOST = 20;
    private static final int AGE_BOOST_PER_HOUR = 5;
    private static final int MAX_AGE_BOOST = 30;

    // ==================== Priority Calculation ====================

    @Override
    public TaskPriorityValue calculatePriority(TaskKey taskKey) {
        Task task = getTask(taskKey);

        int ageMinutes = (int) Duration.between(task.getCreatedAt(), Instant.now()).toMinutes();
        boolean hasSla = task.getDueTime() != null;

        String customerId = task.getContext() != null ? task.getContext().customerId() : null;

        return calculatePriority(task.getTaskType(), customerId, task.getZone(), ageMinutes, hasSla);
    }

    @Override
    public TaskPriorityValue calculatePriority(TaskType type, String customerId, ZoneKey zone,
                                                int taskAgeMinutes, boolean hasSla) {
        int score = BASE_PRIORITY;
        List<String> appliedRules = new ArrayList<>();
        StringBuilder reason = new StringBuilder();

        // Age-based priority boost
        int ageBoost = Math.min((taskAgeMinutes / 60) * AGE_BOOST_PER_HOUR, MAX_AGE_BOOST);
        if (ageBoost > 0) {
            score += ageBoost;
            appliedRules.add("AGE_BOOST");
            reason.append("Age boost: +").append(ageBoost).append("; ");
        }

        // SLA-based priority boost
        if (hasSla) {
            score += SLA_BOOST;
            appliedRules.add("SLA_BOOST");
            reason.append("SLA boost: +").append(SLA_BOOST).append("; ");
        }

        // Apply configured rules
        List<PriorityRule> rules = getActiveRules();
        for (PriorityRule rule : rules) {
            if (rule.appliesTo(type, customerId, zone)) {
                score += rule.getPriorityBoost();
                appliedRules.add(rule.getRuleName());
                reason.append(rule.getRuleName()).append(": +").append(rule.getPriorityBoost()).append("; ");
            }
        }

        TaskPriorityValue.PriorityLevel level = determinePriorityLevel(score);

        return new TaskPriorityValue(score, level, reason.toString().trim());
    }

    @Override
    public void updateTaskPriority(TaskKey taskKey, TaskPriorityValue priority) {
        log.info("Updating priority for task {}: score={}, level={}",
                taskKey.value(), priority.score(), priority.level());

        Task task = getTask(taskKey);
        TaskPriorityValue previousPriority = task.getPriority();

        task.setPriority(priority);
        taskRepository.save(task);

        eventPublisher.publishEvent(new PrioritizationEvents.PriorityCalculated(
                taskKey, previousPriority, priority, List.of(), Instant.now()
        ));

        log.info("Updated priority for task {}", taskKey.value());
    }

    @Override
    public void escalatePriority(TaskKey taskKey, String reason) {
        log.info("Escalating priority for task {}: {}", taskKey.value(), reason);

        Task task = getTask(taskKey);
        TaskPriorityValue current = task.getPriority();
        TaskPriorityValue.PriorityLevel newLevel = escalateLevel(current.level());

        int newScore = current.score() + 25; // Significant boost on escalation
        TaskPriorityValue escalated = new TaskPriorityValue(newScore, newLevel,
                current.reason() + " Escalated: " + reason);

        task.setPriority(escalated);
        taskRepository.save(task);

        eventPublisher.publishEvent(new PrioritizationEvents.PriorityEscalated(
                taskKey, current.level(), newLevel, reason, Instant.now()
        ));

        log.info("Escalated task {} from {} to {}", taskKey.value(), current.level(), newLevel);
    }

    @Override
    public void escalateOverdueTasks() {
        log.info("Escalating overdue tasks");

        List<Task> overdueTasks = taskRepository.findOverdueTasks(LocalDateTime.now());

        for (Task task : overdueTasks) {
            if (task.getPriority().level() != TaskPriorityValue.PriorityLevel.CRITICAL) {
                escalatePriority(task.getTaskKey(), "SLA deadline exceeded");
            }
        }

        log.info("Escalated {} overdue tasks", overdueTasks.size());
    }

    // ==================== Priority Rules ====================

    @Override
    public PriorityRule createRule(String name, PriorityRule.RuleType type,
                                    int priorityBoost, int precedence) {
        log.info("Creating priority rule '{}' of type {}", name, type);

        String ruleKey = UUID.randomUUID().toString();

        PriorityRule rule = PriorityRule.builder()
                .ruleKey(ruleKey)
                .ruleName(name)
                .ruleType(type)
                .priorityBoost(priorityBoost)
                .precedence(precedence)
                .active(true)
                .createdAt(Instant.now())
                .build();

        PriorityRule saved = ruleRepository.save(rule);

        eventPublisher.publishEvent(new PrioritizationEvents.PriorityRuleCreated(
                ruleKey, name, type.name(), priorityBoost, Map.of(), Instant.now(), "SYSTEM"
        ));

        log.info("Created priority rule {}", ruleKey);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public PriorityRule getRule(String ruleKey) {
        return ruleRepository.findByRuleKey(ruleKey)
                .orElseThrow(() -> new PriorityRuleNotFoundException(ruleKey));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriorityRule> getActiveRules() {
        return ruleRepository.findByActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriorityRule> getRulesForTaskType(TaskType taskType) {
        return ruleRepository.findByTaskType(taskType);
    }

    @Override
    public void activateRule(String ruleKey) {
        log.info("Activating rule {}", ruleKey);

        PriorityRule rule = getRule(ruleKey);
        rule.activate();
        ruleRepository.save(rule);

        eventPublisher.publishEvent(new PrioritizationEvents.PriorityRuleActivated(
                ruleKey, Instant.now(), "SYSTEM"
        ));

        log.info("Activated rule {}", ruleKey);
    }

    @Override
    public void deactivateRule(String ruleKey) {
        log.info("Deactivating rule {}", ruleKey);

        PriorityRule rule = getRule(ruleKey);
        rule.deactivate();
        ruleRepository.save(rule);

        eventPublisher.publishEvent(new PrioritizationEvents.PriorityRuleDeactivated(
                ruleKey, "User request", Instant.now(), "SYSTEM"
        ));

        log.info("Deactivated rule {}", ruleKey);
    }

    @Override
    public void updateRule(String ruleKey, PriorityRule updates) {
        log.info("Updating rule {}", ruleKey);

        PriorityRule rule = getRule(ruleKey);
        rule.update(updates);
        ruleRepository.save(rule);

        eventPublisher.publishEvent(new PrioritizationEvents.PriorityRuleUpdated(
                ruleKey, Map.of(), Map.of(), Instant.now(), "SYSTEM"
        ));

        log.info("Updated rule {}", ruleKey);
    }

    @Override
    public void deleteRule(String ruleKey) {
        log.info("Deleting rule {}", ruleKey);

        PriorityRule rule = getRule(ruleKey);
        ruleRepository.delete(rule);

        log.info("Deleted rule {}", ruleKey);
    }

    // ==================== Workload Management ====================

    @Override
    @Transactional(readOnly = true)
    public UserWorkload getUserWorkload(UserKey userId) {
        return workloadRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultWorkload(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserWorkload> getAvailableUsers(ZoneKey zone) {
        return workloadRepository.findByZoneAndStatus(zone, UserWorkload.WorkloadStatus.AVAILABLE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserWorkload> getUsersByStatus(UserWorkload.WorkloadStatus status) {
        return workloadRepository.findByStatus(status);
    }

    @Override
    public void updateUserWorkload(UserKey userId, UserWorkload.WorkloadStatus status) {
        log.info("Updating workload status for user {}: {}", userId.value(), status);

        UserWorkload workload = getUserWorkload(userId);
        workload.setStatus(status);
        workloadRepository.save(workload);

        log.info("Updated workload status for user {}", userId.value());
    }

    @Override
    public void assignTaskToUser(UserKey userId, TaskKey taskKey) {
        log.info("Assigning task {} to user workload {}", taskKey.value(), userId.value());

        UserWorkload workload = getUserWorkload(userId);
        workload.addTask(taskKey);
        workloadRepository.save(workload);

        WorkloadMetrics metrics = calculateWorkloadMetrics(workload);
        eventPublisher.publishEvent(new PrioritizationEvents.WorkloadCalculated(
                userId, metrics, Instant.now()
        ));

        // Check if threshold exceeded
        if (workload.getActiveTasks().size() > workload.getMaxTasks()) {
            eventPublisher.publishEvent(new PrioritizationEvents.WorkloadThresholdExceeded(
                    userId, "ACTIVE_TASKS", workload.getActiveTasks().size(),
                    workload.getMaxTasks(), Instant.now()
            ));
        }

        log.info("Assigned task {} to user workload {}", taskKey.value(), userId.value());
    }

    @Override
    public void completeTaskForUser(UserKey userId, TaskKey taskKey) {
        log.info("Completing task {} for user workload {}", taskKey.value(), userId.value());

        UserWorkload workload = getUserWorkload(userId);
        workload.completeTask(taskKey);
        workloadRepository.save(workload);

        log.info("Completed task {} for user workload {}", taskKey.value(), userId.value());
    }

    @Override
    public void removeTaskFromUser(UserKey userId, TaskKey taskKey) {
        log.info("Removing task {} from user workload {}", taskKey.value(), userId.value());

        UserWorkload workload = getUserWorkload(userId);
        workload.removeTask(taskKey);
        workloadRepository.save(workload);

        log.info("Removed task {} from user workload {}", taskKey.value(), userId.value());
    }

    // ==================== Load Balancing ====================

    @Override
    @Transactional(readOnly = true)
    public Optional<UserKey> findBestUserForTask(TaskKey taskKey, ZoneKey zone) {
        Task task = getTask(taskKey);
        return findBestUserForTask(task.getTaskType(), zone);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserKey> findBestUserForTask(TaskType type, ZoneKey zone) {
        List<UserWorkload> availableUsers = getAvailableUsers(zone);

        return availableUsers.stream()
                .filter(w -> w.canAcceptTask(type))
                .min(Comparator.comparingInt(w -> w.getActiveTasks().size()))
                .map(UserWorkload::getUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserKey> rankUsersForTask(TaskKey taskKey, List<UserKey> candidates) {
        Task task = getTask(taskKey);

        return candidates.stream()
                .map(this::getUserWorkload)
                .filter(w -> w.canAcceptTask(task.getTaskType()))
                .sorted(Comparator
                        .comparingInt((UserWorkload w) -> w.getActiveTasks().size())
                        .thenComparingInt(w -> -w.getSkillLevel(task.getTaskType())))
                .map(UserWorkload::getUserId)
                .collect(Collectors.toList());
    }

    @Override
    public void rebalanceWorkload(ZoneKey zone) {
        log.info("Rebalancing workload in zone {}", zone.value());

        List<UserWorkload> workloads = workloadRepository.findByZone(zone);

        // Find overloaded and underloaded users
        int avgTasks = (int) workloads.stream()
                .mapToInt(w -> w.getActiveTasks().size())
                .average()
                .orElse(0);

        List<UserWorkload> overloaded = workloads.stream()
                .filter(w -> w.getActiveTasks().size() > avgTasks + 2)
                .collect(Collectors.toList());

        List<UserWorkload> underloaded = workloads.stream()
                .filter(w -> w.getActiveTasks().size() < avgTasks - 2)
                .filter(w -> w.getStatus() == UserWorkload.WorkloadStatus.AVAILABLE)
                .collect(Collectors.toList());

        // Transfer tasks from overloaded to underloaded
        for (UserWorkload from : overloaded) {
            if (underloaded.isEmpty()) break;

            UserWorkload to = underloaded.get(0);
            int tasksToTransfer = Math.min(
                    from.getActiveTasks().size() - avgTasks,
                    avgTasks - to.getActiveTasks().size()
            );

            if (tasksToTransfer > 0) {
                List<TaskKey> transferredTasks = new ArrayList<>();
                for (int i = 0; i < tasksToTransfer && !from.getActiveTasks().isEmpty(); i++) {
                    TaskKey task = from.getActiveTasks().get(0);
                    from.removeTask(task);
                    to.addTask(task);
                    transferredTasks.add(task);
                }

                workloadRepository.save(from);
                workloadRepository.save(to);

                eventPublisher.publishEvent(new PrioritizationEvents.WorkloadRebalanced(
                        from.getUserId(), to.getUserId(), transferredTasks,
                        "Zone rebalancing", Instant.now()
                ));
            }

            if (to.getActiveTasks().size() >= avgTasks) {
                underloaded.remove(0);
            }
        }

        log.info("Completed workload rebalancing in zone {}", zone.value());
    }

    // ==================== User Zone Assignment ====================

    @Override
    public void assignUserToZone(UserKey userId, ZoneKey zone) {
        log.info("Assigning user {} to zone {}", userId.value(), zone.value());

        UserWorkload workload = getUserWorkload(userId);
        workload.addZone(zone);
        workloadRepository.save(workload);

        log.info("Assigned user {} to zone {}", userId.value(), zone.value());
    }

    @Override
    public void removeUserFromZone(UserKey userId, ZoneKey zone) {
        log.info("Removing user {} from zone {}", userId.value(), zone.value());

        UserWorkload workload = getUserWorkload(userId);
        workload.removeZone(zone);
        workloadRepository.save(workload);

        log.info("Removed user {} from zone {}", userId.value(), zone.value());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ZoneKey> getUserZones(UserKey userId) {
        UserWorkload workload = getUserWorkload(userId);
        return workload.getAssignedZones();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserKey> getUsersInZone(ZoneKey zone) {
        return workloadRepository.findByZone(zone).stream()
                .map(UserWorkload::getUserId)
                .collect(Collectors.toList());
    }

    // ==================== Metrics ====================

    @Override
    @Transactional(readOnly = true)
    public double getZoneUtilization(ZoneKey zone) {
        List<UserWorkload> workloads = workloadRepository.findByZone(zone);
        if (workloads.isEmpty()) return 0.0;

        int totalCapacity = workloads.stream()
                .mapToInt(UserWorkload::getMaxTasks)
                .sum();

        int currentTasks = workloads.stream()
                .mapToInt(w -> w.getActiveTasks().size())
                .sum();

        return totalCapacity > 0 ? (double) currentTasks / totalCapacity * 100 : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public double getUserUtilization(UserKey userId) {
        UserWorkload workload = getUserWorkload(userId);
        return workload.getMaxTasks() > 0
                ? (double) workload.getActiveTasks().size() / workload.getMaxTasks() * 100
                : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public int getPendingTaskCount(ZoneKey zone) {
        return taskRepository.countByZoneAndStatus(zone,
                com.maersk.wms.task.domain.lifecycle_service.model.TaskStatus.RELEASED);
    }

    // ==================== Private Helpers ====================

    private Task getTask(TaskKey taskKey) {
        return taskRepository.findByTaskKey(taskKey)
                .orElseThrow(() -> new TaskNotFoundException(taskKey.value()));
    }

    private TaskPriorityValue.PriorityLevel determinePriorityLevel(int score) {
        if (score >= 90) return TaskPriorityValue.PriorityLevel.CRITICAL;
        if (score >= 75) return TaskPriorityValue.PriorityLevel.HIGH;
        if (score >= 50) return TaskPriorityValue.PriorityLevel.MEDIUM;
        if (score >= 25) return TaskPriorityValue.PriorityLevel.LOW;
        return TaskPriorityValue.PriorityLevel.NORMAL;
    }

    private TaskPriorityValue.PriorityLevel escalateLevel(TaskPriorityValue.PriorityLevel current) {
        return switch (current) {
            case NORMAL -> TaskPriorityValue.PriorityLevel.LOW;
            case LOW -> TaskPriorityValue.PriorityLevel.MEDIUM;
            case MEDIUM -> TaskPriorityValue.PriorityLevel.HIGH;
            case HIGH, CRITICAL -> TaskPriorityValue.PriorityLevel.CRITICAL;
        };
    }

    private UserWorkload createDefaultWorkload(UserKey userId) {
        UserWorkload workload = UserWorkload.builder()
                .userId(userId)
                .status(UserWorkload.WorkloadStatus.AVAILABLE)
                .activeTasks(new ArrayList<>())
                .assignedZones(new ArrayList<>())
                .maxTasks(10)
                .skillLevels(new HashMap<>())
                .build();
        return workloadRepository.save(workload);
    }

    private WorkloadMetrics calculateWorkloadMetrics(UserWorkload workload) {
        return new WorkloadMetrics(
                workload.getActiveTasks().size(),
                workload.getMaxTasks(),
                workload.getCompletedToday(),
                (double) workload.getActiveTasks().size() / workload.getMaxTasks() * 100
        );
    }
}
