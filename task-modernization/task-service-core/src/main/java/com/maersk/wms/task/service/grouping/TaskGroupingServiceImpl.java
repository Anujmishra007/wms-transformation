package com.maersk.wms.task.service.grouping;

import com.maersk.wms.task.domain.grouping_service.model.*;
import com.maersk.wms.task.domain.grouping_service.repository.*;
import com.maersk.wms.task.domain.grouping_service.service.TaskGroupingService;
import com.maersk.wms.task.domain.grouping_service.event.GroupingEvents;
import com.maersk.wms.task.domain.lifecycle_service.repository.TaskRepository;
import com.maersk.wms.task.shared.kernel.identifiers.*;
import com.maersk.wms.task.shared.kernel.valueobjects.TaskPriorityValue;
import com.maersk.wms.task.shared.kernel.exceptions.TaskGroupNotFoundException;
import com.maersk.wms.task.shared.kernel.exceptions.WorkQueueNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

/**
 * Implementation of Task Grouping Service.
 * Manages task groups (wave, batch, zone, route) and work queues.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TaskGroupingServiceImpl implements TaskGroupingService {

    private final TaskGroupRepository groupRepository;
    private final WorkQueueRepository queueRepository;
    private final TaskRepository taskRepository;
    private final ApplicationEventPublisher eventPublisher;

    // ==================== Task Group Management ====================

    @Override
    public TaskGroup createGroup(TaskGroupType type, String name, ZoneKey zone, TaskPriorityValue priority) {
        log.info("Creating task group '{}' of type {} in zone {}", name, type, zone.value());

        TaskGroupKey groupKey = new TaskGroupKey(UUID.randomUUID().toString());

        TaskGroup group = TaskGroup.builder()
                .groupKey(groupKey)
                .groupType(type)
                .groupName(name)
                .zone(zone)
                .priority(priority)
                .status(TaskGroupStatus.CREATED)
                .taskKeys(new ArrayList<>())
                .createdAt(Instant.now())
                .build();

        TaskGroup savedGroup = groupRepository.save(group);

        eventPublisher.publishEvent(new GroupingEvents.TaskGroupCreated(
                groupKey, type.name(), name, null, Instant.now(), "SYSTEM"
        ));

        log.info("Created task group {}", groupKey.value());
        return savedGroup;
    }

    @Override
    public TaskGroup createWaveGroup(WaveKey waveKey, String name, List<TaskKey> taskKeys) {
        log.info("Creating wave group for wave {}", waveKey.value());

        TaskGroupKey groupKey = new TaskGroupKey(UUID.randomUUID().toString());

        TaskGroup group = TaskGroup.builder()
                .groupKey(groupKey)
                .groupType(TaskGroupType.WAVE)
                .groupName(name)
                .waveKey(waveKey)
                .status(TaskGroupStatus.CREATED)
                .taskKeys(new ArrayList<>(taskKeys))
                .createdAt(Instant.now())
                .build();

        TaskGroup savedGroup = groupRepository.save(group);

        eventPublisher.publishEvent(new GroupingEvents.TaskGroupCreated(
                groupKey, TaskGroupType.WAVE.name(), name, waveKey, Instant.now(), "SYSTEM"
        ));

        taskKeys.forEach(taskKey ->
            eventPublisher.publishEvent(new GroupingEvents.TaskAddedToGroup(
                    groupKey, taskKey, taskKeys.indexOf(taskKey) + 1, Instant.now(), "SYSTEM"
            ))
        );

        log.info("Created wave group {} with {} tasks", groupKey.value(), taskKeys.size());
        return savedGroup;
    }

    @Override
    public TaskGroup createBatchGroup(String batchId, List<TaskKey> taskKeys) {
        log.info("Creating batch group for batch {}", batchId);

        TaskGroupKey groupKey = new TaskGroupKey(UUID.randomUUID().toString());

        TaskGroup group = TaskGroup.builder()
                .groupKey(groupKey)
                .groupType(TaskGroupType.BATCH)
                .groupName("Batch-" + batchId)
                .batchId(batchId)
                .status(TaskGroupStatus.CREATED)
                .taskKeys(new ArrayList<>(taskKeys))
                .createdAt(Instant.now())
                .build();

        TaskGroup savedGroup = groupRepository.save(group);

        eventPublisher.publishEvent(new GroupingEvents.TasksBatched(
                groupKey, taskKeys, "BATCH_" + batchId, Instant.now(), "SYSTEM"
        ));

        log.info("Created batch group {} with {} tasks", groupKey.value(), taskKeys.size());
        return savedGroup;
    }

    @Override
    public TaskGroup createZoneGroup(ZoneKey zone, List<TaskKey> taskKeys) {
        log.info("Creating zone group for zone {}", zone.value());

        TaskGroupKey groupKey = new TaskGroupKey(UUID.randomUUID().toString());

        TaskGroup group = TaskGroup.builder()
                .groupKey(groupKey)
                .groupType(TaskGroupType.ZONE)
                .groupName("Zone-" + zone.value())
                .zone(zone)
                .status(TaskGroupStatus.CREATED)
                .taskKeys(new ArrayList<>(taskKeys))
                .createdAt(Instant.now())
                .build();

        TaskGroup savedGroup = groupRepository.save(group);

        log.info("Created zone group {} with {} tasks", groupKey.value(), taskKeys.size());
        return savedGroup;
    }

    @Override
    public TaskGroup createRouteGroup(String routeId, List<TaskKey> taskKeys) {
        log.info("Creating route group for route {}", routeId);

        TaskGroupKey groupKey = new TaskGroupKey(UUID.randomUUID().toString());

        TaskGroup group = TaskGroup.builder()
                .groupKey(groupKey)
                .groupType(TaskGroupType.ROUTE)
                .groupName("Route-" + routeId)
                .routeId(routeId)
                .status(TaskGroupStatus.CREATED)
                .taskKeys(new ArrayList<>(taskKeys))
                .createdAt(Instant.now())
                .build();

        TaskGroup savedGroup = groupRepository.save(group);

        log.info("Created route group {} with {} tasks", groupKey.value(), taskKeys.size());
        return savedGroup;
    }

    // ==================== Group Operations ====================

    @Override
    @Transactional(readOnly = true)
    public TaskGroup getGroup(TaskGroupKey groupKey) {
        return groupRepository.findByGroupKey(groupKey)
                .orElseThrow(() -> new TaskGroupNotFoundException(groupKey.value()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskGroup> getGroupsByStatus(TaskGroupStatus status) {
        return groupRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskGroup> getGroupsByWave(WaveKey waveKey) {
        return groupRepository.findByWaveKey(waveKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskGroup> getGroupsByZone(ZoneKey zone) {
        return groupRepository.findByZone(zone);
    }

    @Override
    public void addTaskToGroup(TaskGroupKey groupKey, TaskKey taskKey) {
        log.info("Adding task {} to group {}", taskKey.value(), groupKey.value());

        TaskGroup group = getGroup(groupKey);
        int sequence = group.getTaskKeys().size() + 1;

        group.addTask(taskKey);
        groupRepository.save(group);

        eventPublisher.publishEvent(new GroupingEvents.TaskAddedToGroup(
                groupKey, taskKey, sequence, Instant.now(), "SYSTEM"
        ));

        log.info("Added task {} to group {}", taskKey.value(), groupKey.value());
    }

    @Override
    public void removeTaskFromGroup(TaskGroupKey groupKey, TaskKey taskKey) {
        log.info("Removing task {} from group {}", taskKey.value(), groupKey.value());

        TaskGroup group = getGroup(groupKey);
        group.removeTask(taskKey);
        groupRepository.save(group);

        eventPublisher.publishEvent(new GroupingEvents.TaskRemovedFromGroup(
                groupKey, taskKey, "User request", Instant.now(), "SYSTEM"
        ));

        log.info("Removed task {} from group {}", taskKey.value(), groupKey.value());
    }

    @Override
    public void releaseGroup(TaskGroupKey groupKey) {
        log.info("Releasing group {}", groupKey.value());

        TaskGroup group = getGroup(groupKey);
        group.release();
        groupRepository.save(group);

        eventPublisher.publishEvent(new GroupingEvents.TaskGroupReleased(
                groupKey, group.getTaskKeys().size(), Instant.now(), "SYSTEM"
        ));

        log.info("Released group {}", groupKey.value());
    }

    @Override
    public void assignGroup(TaskGroupKey groupKey, UserKey userId, DeviceKey deviceId) {
        log.info("Assigning group {} to user {}", groupKey.value(), userId.value());

        TaskGroup group = getGroup(groupKey);
        group.assign(userId, deviceId);
        groupRepository.save(group);

        log.info("Assigned group {} to user {}", groupKey.value(), userId.value());
    }

    @Override
    public void suspendGroup(TaskGroupKey groupKey, String reason) {
        log.info("Suspending group {} - reason: {}", groupKey.value(), reason);

        TaskGroup group = getGroup(groupKey);
        group.suspend(reason);
        groupRepository.save(group);

        log.info("Suspended group {}", groupKey.value());
    }

    @Override
    public void cancelGroup(TaskGroupKey groupKey, String reason) {
        log.info("Cancelling group {} - reason: {}", groupKey.value(), reason);

        TaskGroup group = getGroup(groupKey);
        group.cancel(reason);
        groupRepository.save(group);

        eventPublisher.publishEvent(new GroupingEvents.TaskGroupCancelled(
                groupKey, reason, Instant.now(), "SYSTEM"
        ));

        log.info("Cancelled group {}", groupKey.value());
    }

    // ==================== Group Progress ====================

    @Override
    public void markTaskCompleted(TaskGroupKey groupKey, TaskKey taskKey) {
        log.info("Marking task {} completed in group {}", taskKey.value(), groupKey.value());

        TaskGroup group = getGroup(groupKey);
        group.markTaskCompleted(taskKey);

        if (group.isComplete()) {
            group.complete();
            eventPublisher.publishEvent(new GroupingEvents.TaskGroupCompleted(
                    groupKey, group.getCompletedTasks(), group.getTotalTasks(), Instant.now()
            ));
        }

        groupRepository.save(group);
    }

    @Override
    public void markTaskCancelled(TaskGroupKey groupKey, TaskKey taskKey) {
        log.info("Marking task {} cancelled in group {}", taskKey.value(), groupKey.value());

        TaskGroup group = getGroup(groupKey);
        group.markTaskCancelled(taskKey);
        groupRepository.save(group);
    }

    @Override
    @Transactional(readOnly = true)
    public double getGroupProgress(TaskGroupKey groupKey) {
        TaskGroup group = getGroup(groupKey);
        return group.getProgress();
    }

    // ==================== Work Queue Management ====================

    @Override
    public WorkQueue createWorkQueue(String name, ZoneKey zone, WorkQueue.QueueStrategy strategy, int maxCapacity) {
        log.info("Creating work queue '{}' in zone {} with strategy {}", name, zone.value(), strategy);

        WorkQueueKey queueKey = new WorkQueueKey(UUID.randomUUID().toString());

        WorkQueue queue = WorkQueue.builder()
                .queueKey(queueKey)
                .queueName(name)
                .zone(zone)
                .strategy(strategy)
                .maxCapacity(maxCapacity)
                .status(WorkQueue.QueueStatus.ACTIVE)
                .taskKeys(new ArrayList<>())
                .assignedUsers(new ArrayList<>())
                .createdAt(Instant.now())
                .build();

        WorkQueue savedQueue = queueRepository.save(queue);

        eventPublisher.publishEvent(new GroupingEvents.WorkQueueCreated(
                queueKey, name, "WORK_QUEUE", zone, strategy.name(), Instant.now(), "SYSTEM"
        ));

        log.info("Created work queue {}", queueKey.value());
        return savedQueue;
    }

    @Override
    @Transactional(readOnly = true)
    public WorkQueue getWorkQueue(WorkQueueKey queueKey) {
        return queueRepository.findByQueueKey(queueKey)
                .orElseThrow(() -> new WorkQueueNotFoundException(queueKey.value()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkQueue> getActiveQueues() {
        return queueRepository.findByStatus(WorkQueue.QueueStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkQueue> getQueuesByZone(ZoneKey zone) {
        return queueRepository.findByZone(zone);
    }

    @Override
    public void addTaskToQueue(WorkQueueKey queueKey, TaskKey taskKey) {
        log.info("Adding task {} to queue {}", taskKey.value(), queueKey.value());

        WorkQueue queue = getWorkQueue(queueKey);
        int position = queue.addTask(taskKey);
        queueRepository.save(queue);

        eventPublisher.publishEvent(new GroupingEvents.TaskEnqueued(
                queueKey, taskKey, 0, position, Instant.now()
        ));

        log.info("Added task {} to queue {} at position {}", taskKey.value(), queueKey.value(), position);
    }

    @Override
    public void removeTaskFromQueue(WorkQueueKey queueKey, TaskKey taskKey) {
        log.info("Removing task {} from queue {}", taskKey.value(), queueKey.value());

        WorkQueue queue = getWorkQueue(queueKey);
        queue.removeTask(taskKey);
        queueRepository.save(queue);

        log.info("Removed task {} from queue {}", taskKey.value(), queueKey.value());
    }

    @Override
    public Optional<TaskKey> getNextTaskFromQueue(WorkQueueKey queueKey, UserKey userId) {
        WorkQueue queue = getWorkQueue(queueKey);
        Optional<TaskKey> nextTask = queue.getNextTask();

        if (nextTask.isPresent()) {
            queue.removeTask(nextTask.get());
            queueRepository.save(queue);

            eventPublisher.publishEvent(new GroupingEvents.TaskDequeued(
                    queueKey, nextTask.get(), userId, Instant.now()
            ));
        }

        return nextTask;
    }

    @Override
    public void pauseQueue(WorkQueueKey queueKey) {
        log.info("Pausing queue {}", queueKey.value());

        WorkQueue queue = getWorkQueue(queueKey);
        queue.pause();
        queueRepository.save(queue);

        eventPublisher.publishEvent(new GroupingEvents.WorkQueuePaused(
                queueKey, "User request", Instant.now(), "SYSTEM"
        ));

        log.info("Paused queue {}", queueKey.value());
    }

    @Override
    public void resumeQueue(WorkQueueKey queueKey) {
        log.info("Resuming queue {}", queueKey.value());

        WorkQueue queue = getWorkQueue(queueKey);
        queue.resume();
        queueRepository.save(queue);

        eventPublisher.publishEvent(new GroupingEvents.WorkQueueResumed(
                queueKey, Instant.now(), "SYSTEM"
        ));

        log.info("Resumed queue {}", queueKey.value());
    }

    @Override
    public void closeQueue(WorkQueueKey queueKey) {
        log.info("Closing queue {}", queueKey.value());

        WorkQueue queue = getWorkQueue(queueKey);
        queue.close();
        queueRepository.save(queue);

        log.info("Closed queue {}", queueKey.value());
    }

    // ==================== Queue User Management ====================

    @Override
    public void addUserToQueue(WorkQueueKey queueKey, UserKey userId) {
        log.info("Adding user {} to queue {}", userId.value(), queueKey.value());

        WorkQueue queue = getWorkQueue(queueKey);
        queue.addUser(userId);
        queueRepository.save(queue);

        log.info("Added user {} to queue {}", userId.value(), queueKey.value());
    }

    @Override
    public void removeUserFromQueue(WorkQueueKey queueKey, UserKey userId) {
        log.info("Removing user {} from queue {}", userId.value(), queueKey.value());

        WorkQueue queue = getWorkQueue(queueKey);
        queue.removeUser(userId);
        queueRepository.save(queue);

        log.info("Removed user {} from queue {}", userId.value(), queueKey.value());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserKey> getQueueUsers(WorkQueueKey queueKey) {
        WorkQueue queue = getWorkQueue(queueKey);
        return queue.getAssignedUsers();
    }

    // ==================== Metrics ====================

    @Override
    @Transactional(readOnly = true)
    public int getQueueDepth(WorkQueueKey queueKey) {
        WorkQueue queue = getWorkQueue(queueKey);
        return queue.getTaskKeys().size();
    }

    @Override
    @Transactional(readOnly = true)
    public int getGroupPendingTasks(TaskGroupKey groupKey) {
        TaskGroup group = getGroup(groupKey);
        return group.getPendingTasks();
    }
}
