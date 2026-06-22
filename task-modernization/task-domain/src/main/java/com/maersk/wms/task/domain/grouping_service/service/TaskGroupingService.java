package com.maersk.wms.task.domain.grouping_service.service;

import com.maersk.wms.task.domain.grouping_service.model.*;
import com.maersk.wms.task.shared.kernel.identifiers.*;
import com.maersk.wms.task.shared.kernel.valueobjects.TaskPriorityValue;

import java.util.List;
import java.util.Optional;

/**
 * Task Grouping Service - manages task groups and work queues.
 */
public interface TaskGroupingService {

    // Task Group Management
    TaskGroup createGroup(TaskGroupType type, String name, ZoneKey zone, TaskPriorityValue priority);
    TaskGroup createWaveGroup(WaveKey waveKey, String name, List<TaskKey> taskKeys);
    TaskGroup createBatchGroup(String batchId, List<TaskKey> taskKeys);
    TaskGroup createZoneGroup(ZoneKey zone, List<TaskKey> taskKeys);
    TaskGroup createRouteGroup(String routeId, List<TaskKey> taskKeys);

    // Group Operations
    TaskGroup getGroup(TaskGroupKey groupKey);
    List<TaskGroup> getGroupsByStatus(TaskGroupStatus status);
    List<TaskGroup> getGroupsByWave(WaveKey waveKey);
    List<TaskGroup> getGroupsByZone(ZoneKey zone);

    void addTaskToGroup(TaskGroupKey groupKey, TaskKey taskKey);
    void removeTaskFromGroup(TaskGroupKey groupKey, TaskKey taskKey);
    void releaseGroup(TaskGroupKey groupKey);
    void assignGroup(TaskGroupKey groupKey, UserKey userId, DeviceKey deviceId);
    void suspendGroup(TaskGroupKey groupKey, String reason);
    void cancelGroup(TaskGroupKey groupKey, String reason);

    // Group Progress
    void markTaskCompleted(TaskGroupKey groupKey, TaskKey taskKey);
    void markTaskCancelled(TaskGroupKey groupKey, TaskKey taskKey);
    double getGroupProgress(TaskGroupKey groupKey);

    // Work Queue Management
    WorkQueue createWorkQueue(String name, ZoneKey zone, WorkQueue.QueueStrategy strategy, int maxCapacity);
    WorkQueue getWorkQueue(WorkQueueKey queueKey);
    List<WorkQueue> getActiveQueues();
    List<WorkQueue> getQueuesByZone(ZoneKey zone);

    void addTaskToQueue(WorkQueueKey queueKey, TaskKey taskKey);
    void removeTaskFromQueue(WorkQueueKey queueKey, TaskKey taskKey);
    Optional<TaskKey> getNextTaskFromQueue(WorkQueueKey queueKey, UserKey userId);
    void pauseQueue(WorkQueueKey queueKey);
    void resumeQueue(WorkQueueKey queueKey);
    void closeQueue(WorkQueueKey queueKey);

    // Queue User Management
    void addUserToQueue(WorkQueueKey queueKey, UserKey userId);
    void removeUserFromQueue(WorkQueueKey queueKey, UserKey userId);
    List<UserKey> getQueueUsers(WorkQueueKey queueKey);

    // Metrics
    int getQueueDepth(WorkQueueKey queueKey);
    int getGroupPendingTasks(TaskGroupKey groupKey);
}
