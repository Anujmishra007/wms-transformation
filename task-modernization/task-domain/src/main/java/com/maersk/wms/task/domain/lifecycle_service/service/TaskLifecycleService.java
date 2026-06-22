package com.maersk.wms.task.domain.lifecycle_service.service;

import com.maersk.wms.task.domain.lifecycle_service.model.*;
import com.maersk.wms.task.shared.kernel.identifiers.*;
import com.maersk.wms.task.shared.kernel.valueobjects.TaskContext;
import com.maersk.wms.task.shared.kernel.valueobjects.TaskPriorityValue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Task Lifecycle Service - manages task creation, assignment, and execution.
 */
public interface TaskLifecycleService {

    // Task Creation
    Task createTask(TaskType type, String sourceType, String sourceKey,
                    LocationKey fromLocation, LocationKey toLocation,
                    SkuKey sku, BigDecimal quantity, TaskPriorityValue priority);

    Task createTask(CreateTaskRequest request);

    // Task Retrieval
    Task getTask(TaskKey taskKey);
    List<Task> getTasksByStatus(TaskStatus status);
    List<Task> getTasksByZone(ZoneKey zone, TaskStatus status);
    List<Task> getTasksByType(TaskType type, TaskStatus status);
    List<Task> getTasksByUser(UserKey userId);
    List<Task> getTasksByGroup(TaskGroupKey groupKey);
    List<Task> getOverdueTasks();

    // Task Release
    void releaseTask(TaskKey taskKey);
    void releaseTasks(List<TaskKey> taskKeys);

    // Task Assignment
    void assignTask(TaskKey taskKey, UserKey userId, DeviceKey deviceId);
    void unassignTask(TaskKey taskKey, String reason);
    void reassignTask(TaskKey taskKey, UserKey newUserId);
    Optional<Task> getNextTaskForUser(UserKey userId, ZoneKey zone);

    // Task Execution
    void startTask(TaskKey taskKey);
    void suspendTask(TaskKey taskKey, String reason);
    void resumeTask(TaskKey taskKey);
    void completeTask(TaskKey taskKey, BigDecimal actualQuantity);
    void cancelTask(TaskKey taskKey, String reason);
    void closeTask(TaskKey taskKey);

    // Bulk Operations
    void closeTasks(List<TaskKey> taskKeys);
    void cancelTasks(List<TaskKey> taskKeys, String reason);

    // Task History
    List<TaskHistory> getTaskHistory(TaskKey taskKey);
    List<TaskHistory> getTaskHistoryByUser(UserKey userId, LocalDateTime from, LocalDateTime to);

    // Metrics
    int countTasksByStatus(TaskStatus status);
    int countTasksByZoneAndStatus(ZoneKey zone, TaskStatus status);

    /**
     * Request to create a task.
     */
    record CreateTaskRequest(
            TaskType taskType,
            String sourceType,
            String sourceKey,
            LocationKey fromLocation,
            LocationKey toLocation,
            ZoneKey zone,
            LpnKey lpn,
            SkuKey sku,
            BigDecimal quantity,
            String uom,
            TaskPriorityValue priority,
            TaskGroupKey groupKey,
            WorkQueueKey queueKey,
            TaskContext context,
            String instructions,
            LocalDateTime dueTime
    ) {}
}
