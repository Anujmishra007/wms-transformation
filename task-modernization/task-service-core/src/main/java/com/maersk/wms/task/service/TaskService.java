package com.maersk.wms.task.service;

import com.maersk.wms.task.domain.entity.Task;
import com.maersk.wms.task.domain.entity.UserWorkload;
import com.maersk.wms.task.domain.enums.TaskStatus;
import com.maersk.wms.task.domain.enums.TaskPriority;
import com.maersk.wms.task.domain.repository.TaskRepository;
import com.maersk.wms.task.domain.repository.UserWorkloadRepository;
import com.maersk.wms.task.plugin.*;
import com.maersk.wms.task.rules.TaskRulesEngine;
import com.maersk.wms.task.rules.facts.TaskPriorityFacts;
import com.maersk.wms.task.rules.facts.TaskPriorityResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Core service for task management operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserWorkloadRepository userWorkloadRepository;
    private final TaskPluginRegistry pluginRegistry;
    private final TaskRulesEngine rulesEngine;

    @Transactional
    public Task createTask(Task task, TaskPluginContext context) {
        log.info("Creating task: {} for client: {}", task.getTaskId(), context.getClientCode());

        // Execute before-create plugins
        PluginResult pluginResult = pluginRegistry.executePlugins(
                TaskCreationPlugin.class,
                context.getClientCode(),
                plugin -> plugin.beforeTaskCreate(task, context)
        );

        if (!pluginResult.isSuccess()) {
            throw new TaskOperationException(pluginResult.getErrorCode(), pluginResult.getMessage());
        }

        // Validate task
        pluginResult = pluginRegistry.executePlugins(
                TaskCreationPlugin.class,
                context.getClientCode(),
                plugin -> plugin.validateTask(task, context)
        );

        if (!pluginResult.isSuccess()) {
            throw new TaskOperationException(pluginResult.getErrorCode(), pluginResult.getMessage());
        }

        // Determine priority using rules engine
        TaskPriority priority = determinePriority(task, context);
        task.setPriority(priority);

        // Set defaults
        task.setStatus(TaskStatus.CREATED);
        task.setCreatedAt(LocalDateTime.now());
        task.setCreatedBy(context.getUserId());
        task.setClientCode(context.getClientCode());
        task.setFacilityCode(context.getFacilityCode());

        // Transform task (client-specific transformations)
        Task transformedTask = task;
        for (TaskCreationPlugin plugin : pluginRegistry.getTaskCreationPlugins(context.getClientCode())) {
            transformedTask = plugin.transformTask(transformedTask, context);
        }

        // Save task
        Task savedTask = taskRepository.save(transformedTask);

        // Execute after-create plugins
        pluginRegistry.executePlugins(
                TaskCreationPlugin.class,
                context.getClientCode(),
                plugin -> plugin.afterTaskCreate(savedTask, context)
        );

        log.info("Task created successfully: {} with priority: {}", savedTask.getTaskKey(), savedTask.getPriority());
        return savedTask;
    }

    @Transactional
    public void assignTask(Long taskKey, String userId, TaskPluginContext context) {
        log.info("Assigning task {} to user {}", taskKey, userId);

        Task task = taskRepository.findByTaskKey(taskKey)
                .orElseThrow(() -> new TaskOperationException("TASK_NOT_FOUND", "Task not found: " + taskKey));

        if (!task.getStatus().canBeAssigned()) {
            throw new TaskOperationException("INVALID_STATUS", "Task cannot be assigned in status: " + task.getStatus());
        }

        // Execute before-assignment plugins
        PluginResult pluginResult = pluginRegistry.executePlugins(
                TaskAssignmentPlugin.class,
                context.getClientCode(),
                plugin -> plugin.beforeAssignment(task, userId, context)
        );

        if (!pluginResult.isSuccess()) {
            throw new TaskOperationException(pluginResult.getErrorCode(), pluginResult.getMessage());
        }

        // Validate assignment
        pluginResult = pluginRegistry.executePlugins(
                TaskAssignmentPlugin.class,
                context.getClientCode(),
                plugin -> plugin.validateAssignment(task, userId, context)
        );

        if (!pluginResult.isSuccess()) {
            throw new TaskOperationException(pluginResult.getErrorCode(), pluginResult.getMessage());
        }

        // Perform assignment
        taskRepository.assignTask(taskKey, userId, getUserName(userId));
        taskRepository.updateStatus(taskKey, TaskStatus.ASSIGNED, context.getUserId());

        // Update user workload
        userWorkloadRepository.updateTaskCounts(userId,
                userWorkloadRepository.findByUserId(userId)
                        .map(w -> w.getAssignedTasks() + 1)
                        .orElse(1),
                0, 0, 0);

        log.info("Task {} assigned to user {}", taskKey, userId);
    }

    @Transactional
    public void completeTask(Long taskKey, Double completedQuantity, TaskPluginContext context) {
        log.info("Completing task {} with quantity {}", taskKey, completedQuantity);

        Task task = taskRepository.findByTaskKey(taskKey)
                .orElseThrow(() -> new TaskOperationException("TASK_NOT_FOUND", "Task not found: " + taskKey));

        // Validate completion
        PluginResult pluginResult = pluginRegistry.executePlugins(
                TaskExecutionPlugin.class,
                context.getClientCode(),
                plugin -> plugin.validateCompletion(task, completedQuantity, context)
        );

        if (!pluginResult.isSuccess()) {
            throw new TaskOperationException(pluginResult.getErrorCode(), pluginResult.getMessage());
        }

        // Complete task
        taskRepository.completeTask(taskKey, completedQuantity, context.getUserId());

        // Execute on-complete plugins
        pluginRegistry.executePlugins(
                TaskExecutionPlugin.class,
                context.getClientCode(),
                plugin -> plugin.onTaskCompleted(task, context)
        );

        log.info("Task {} completed successfully", taskKey);
    }

    public Task getTask(Long taskKey) {
        return taskRepository.findByTaskKey(taskKey)
                .orElseThrow(() -> new TaskOperationException("TASK_NOT_FOUND", "Task not found: " + taskKey));
    }

    public Task getTaskById(String taskId) {
        return taskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new TaskOperationException("TASK_NOT_FOUND", "Task not found: " + taskId));
    }

    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    public List<Task> getTasksByAssignedUser(String userId) {
        return taskRepository.findByAssignedUserId(userId);
    }

    public List<Task> getUnassignedTasks() {
        return taskRepository.findUnassignedTasks();
    }

    public List<Task> getUnassignedTasksByWorkZone(String workZone) {
        return taskRepository.findUnassignedTasksByWorkZone(workZone);
    }

    private TaskPriority determinePriority(Task task, TaskPluginContext context) {
        TaskPriorityFacts facts = TaskPriorityFacts.builder()
                .taskId(task.getTaskId())
                .taskType(task.getTaskType())
                .currentPriority(task.getPriority())
                .dueDate(task.getDueDate())
                .quantity(task.getQuantity())
                .clientCode(context.getClientCode())
                .facilityCode(context.getFacilityCode())
                .build();

        TaskPriorityResult result = rulesEngine.evaluatePriority(facts);

        // Allow plugins to override
        for (TaskCreationPlugin plugin : pluginRegistry.getTaskCreationPlugins(context.getClientCode())) {
            TaskPriority pluginPriority = plugin.determinePriority(task, context);
            if (pluginPriority != null && pluginPriority.isHigherThan(result.getRecommendedPriority())) {
                result.setRecommendedPriority(pluginPriority);
            }
        }

        return result.getRecommendedPriority();
    }

    private String getUserName(String userId) {
        return userWorkloadRepository.findByUserId(userId)
                .map(UserWorkload::getUserName)
                .orElse(userId);
    }
}
