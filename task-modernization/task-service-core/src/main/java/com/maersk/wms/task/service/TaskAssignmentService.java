package com.maersk.wms.task.service;

import com.maersk.wms.task.domain.entity.Task;
import com.maersk.wms.task.domain.entity.UserWorkload;
import com.maersk.wms.task.domain.repository.TaskRepository;
import com.maersk.wms.task.domain.repository.UserWorkloadRepository;
import com.maersk.wms.task.plugin.*;
import com.maersk.wms.task.rules.TaskRulesEngine;
import com.maersk.wms.task.rules.facts.TaskAssignmentFacts;
import com.maersk.wms.task.rules.facts.TaskAssignmentResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * Service for automated task assignment.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskAssignmentService {

    private final TaskRepository taskRepository;
    private final UserWorkloadRepository userWorkloadRepository;
    private final TaskPluginRegistry pluginRegistry;
    private final TaskRulesEngine rulesEngine;

    /**
     * Automatically assigns a task to the best available user.
     */
    @Transactional
    public String autoAssignTask(Task task, TaskPluginContext context) {
        log.info("Auto-assigning task {} in zone {}", task.getTaskId(), task.getWorkZone());

        // Check if auto-assignment should be used
        for (TaskAssignmentPlugin plugin : pluginRegistry.getTaskAssignmentPlugins(context.getClientCode())) {
            if (!plugin.shouldAutoAssign(task, context)) {
                log.info("Auto-assignment disabled by plugin {} for task {}", plugin.getPluginName(), task.getTaskId());
                return null;
            }
        }

        // Get available users
        List<UserWorkload> availableUsers = getAvailableUsers(task, context);

        if (availableUsers.isEmpty()) {
            log.warn("No available users for task {}", task.getTaskId());
            return null;
        }

        // Let plugins select user
        for (TaskAssignmentPlugin plugin : pluginRegistry.getTaskAssignmentPlugins(context.getClientCode())) {
            String selectedUserId = plugin.selectUser(task, availableUsers, context);
            if (selectedUserId != null) {
                log.info("Plugin {} selected user {} for task {}", plugin.getPluginName(), selectedUserId, task.getTaskId());
                return selectedUserId;
            }
        }

        // Fall back to rules-based selection
        return selectBestUserByRules(task, availableUsers, context);
    }

    /**
     * Selects the best user using load balancing (least loaded).
     */
    public String selectLeastLoadedUser(String workGroup) {
        UserWorkload leastLoaded = userWorkloadRepository.findLeastLoadedUser(workGroup);
        return leastLoaded != null ? leastLoaded.getUserId() : null;
    }

    /**
     * Selects the best user using zone proximity.
     */
    public String selectUserByZoneProximity(Task task, List<UserWorkload> users) {
        String taskZone = task.getSourceZone();

        return users.stream()
                .filter(u -> taskZone.equals(u.getCurrentZone()))
                .min(Comparator.comparingInt(UserWorkload::getAssignedTasks))
                .map(UserWorkload::getUserId)
                .orElse(users.stream()
                        .min(Comparator.comparingInt(UserWorkload::getAssignedTasks))
                        .map(UserWorkload::getUserId)
                        .orElse(null));
    }

    /**
     * Validates if a user can accept a task.
     */
    public boolean validateUserCanAcceptTask(String userId, Task task, TaskPluginContext context) {
        UserWorkload workload = userWorkloadRepository.findByUserId(userId).orElse(null);

        if (workload == null || !workload.getIsAvailable()) {
            return false;
        }

        // Check capacity
        if (workload.getAssignedTasks() >= workload.getMaxConcurrentTasks()) {
            return false;
        }

        // Check allowed task types
        if (workload.getAllowedTaskTypes() != null &&
                !workload.getAllowedTaskTypes().isEmpty() &&
                !workload.getAllowedTaskTypes().contains(task.getTaskType().name())) {
            return false;
        }

        // Validate with plugins
        PluginResult result = pluginRegistry.executePlugins(
                TaskAssignmentPlugin.class,
                context.getClientCode(),
                plugin -> plugin.validateAssignment(task, userId, context)
        );

        return result.isSuccess();
    }

    private List<UserWorkload> getAvailableUsers(Task task, TaskPluginContext context) {
        List<UserWorkload> users;

        if (task.getWorkGroup() != null) {
            users = userWorkloadRepository.findAvailableUsersByWorkGroup(task.getWorkGroup());
        } else if (task.getWorkZone() != null) {
            users = userWorkloadRepository.findAvailableUsersByWorkZone(task.getWorkZone());
        } else {
            users = userWorkloadRepository.findAvailableUsers();
        }

        // Filter by task type capability
        return users.stream()
                .filter(u -> u.getAllowedTaskTypes() == null ||
                        u.getAllowedTaskTypes().isEmpty() ||
                        u.getAllowedTaskTypes().contains(task.getTaskType().name()))
                .filter(u -> u.getAssignedTasks() < u.getMaxConcurrentTasks())
                .toList();
    }

    private String selectBestUserByRules(Task task, List<UserWorkload> users, TaskPluginContext context) {
        String bestUser = null;
        int highestScore = Integer.MIN_VALUE;

        for (UserWorkload user : users) {
            TaskAssignmentFacts facts = buildAssignmentFacts(task, user, context);
            TaskAssignmentResult result = rulesEngine.evaluateAssignment(facts);

            if (result.getIsEligible() && result.getAssignmentScore() > highestScore) {
                highestScore = result.getAssignmentScore();
                bestUser = user.getUserId();
            }
        }

        return bestUser;
    }

    private TaskAssignmentFacts buildAssignmentFacts(Task task, UserWorkload user, TaskPluginContext context) {
        return TaskAssignmentFacts.builder()
                .taskId(task.getTaskId())
                .taskType(task.getTaskType())
                .priority(task.getPriority())
                .sourceLocation(task.getSourceLocation())
                .sourceZone(task.getSourceZone())
                .quantity(task.getQuantity())
                .userId(user.getUserId())
                .userCurrentLocation(user.getCurrentLocation())
                .userCurrentZone(user.getCurrentZone())
                .userWorkGroup(user.getWorkGroup())
                .userCurrentTaskCount(user.getAssignedTasks())
                .userMaxTasks(user.getMaxConcurrentTasks())
                .userProductivityRate(user.getProductivityRate())
                .clientCode(context.getClientCode())
                .facilityCode(context.getFacilityCode())
                .build();
    }
}
