package com.maersk.wms.task.service.lifecycle;

import com.maersk.wms.task.domain.lifecycle_service.model.*;
import com.maersk.wms.task.domain.lifecycle_service.repository.*;
import com.maersk.wms.task.domain.lifecycle_service.service.TaskLifecycleService;
import com.maersk.wms.task.domain.lifecycle_service.event.LifecycleEvents;
import com.maersk.wms.task.domain.grouping_service.repository.WorkQueueRepository;
import com.maersk.wms.task.shared.kernel.identifiers.*;
import com.maersk.wms.task.shared.kernel.valueobjects.TaskContext;
import com.maersk.wms.task.shared.kernel.valueobjects.TaskPriorityValue;
import com.maersk.wms.task.shared.kernel.exceptions.TaskNotFoundException;
import com.maersk.wms.task.shared.kernel.exceptions.InvalidTaskStateException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementation of Task Lifecycle Service.
 * Manages the complete lifecycle of tasks from creation to completion.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TaskLifecycleServiceImpl implements TaskLifecycleService {

    private final TaskRepository taskRepository;
    private final TaskHistoryRepository historyRepository;
    private final TaskAssignmentRepository assignmentRepository;
    private final WorkQueueRepository workQueueRepository;
    private final ApplicationEventPublisher eventPublisher;

    // ==================== Task Creation ====================

    @Override
    public Task createTask(TaskType type, String sourceType, String sourceKey,
                           LocationKey fromLocation, LocationKey toLocation,
                           SkuKey sku, BigDecimal quantity, TaskPriorityValue priority) {

        CreateTaskRequest request = new CreateTaskRequest(
                type, sourceType, sourceKey, fromLocation, toLocation,
                null, null, sku, quantity, null, priority, null, null, null, null, null
        );
        return createTask(request);
    }

    @Override
    public Task createTask(CreateTaskRequest request) {
        log.info("Creating task of type {} for source {}/{}",
                request.taskType(), request.sourceType(), request.sourceKey());

        TaskKey taskKey = new TaskKey(UUID.randomUUID().toString());

        Task task = Task.builder()
                .taskKey(taskKey)
                .taskType(request.taskType())
                .status(TaskStatus.CREATED)
                .sourceType(request.sourceType())
                .sourceKey(request.sourceKey())
                .fromLocation(request.fromLocation())
                .toLocation(request.toLocation())
                .zone(request.zone())
                .lpn(request.lpn())
                .sku(request.sku())
                .quantity(request.quantity())
                .uom(request.uom())
                .priority(request.priority())
                .groupKey(request.groupKey())
                .queueKey(request.queueKey())
                .context(request.context())
                .instructions(request.instructions())
                .dueTime(request.dueTime())
                .createdAt(Instant.now())
                .build();

        Task savedTask = taskRepository.save(task);

        recordHistory(taskKey, null, TaskStatus.CREATED, "TASK_CREATED", "SYSTEM");

        eventPublisher.publishEvent(new LifecycleEvents.TaskCreated(
                taskKey,
                request.taskType().name(),
                request.sourceType(),
                request.sourceKey(),
                request.fromLocation(),
                request.toLocation(),
                request.priority(),
                Instant.now(),
                "SYSTEM"
        ));

        log.info("Created task {} of type {}", taskKey.value(), request.taskType());
        return savedTask;
    }

    // ==================== Task Retrieval ====================

    @Override
    @Transactional(readOnly = true)
    public Task getTask(TaskKey taskKey) {
        return taskRepository.findByTaskKey(taskKey)
                .orElseThrow(() -> new TaskNotFoundException(taskKey.value()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByZone(ZoneKey zone, TaskStatus status) {
        return taskRepository.findByZoneAndStatus(zone, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByType(TaskType type, TaskStatus status) {
        return taskRepository.findByTypeAndStatus(type, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByUser(UserKey userId) {
        List<TaskAssignment> assignments = assignmentRepository.findByUserIdAndActive(userId);
        return assignments.stream()
                .map(a -> taskRepository.findByTaskKey(a.getTaskKey()).orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByGroup(TaskGroupKey groupKey) {
        return taskRepository.findByGroupKey(groupKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getOverdueTasks() {
        return taskRepository.findOverdueTasks(LocalDateTime.now());
    }

    // ==================== Task Release ====================

    @Override
    public void releaseTask(TaskKey taskKey) {
        log.info("Releasing task {}", taskKey.value());

        Task task = getTask(taskKey);
        validateStateTransition(task, TaskStatus.RELEASED);

        task.release();
        taskRepository.save(task);

        recordHistory(taskKey, TaskStatus.CREATED, TaskStatus.RELEASED, "TASK_RELEASED", "SYSTEM");

        eventPublisher.publishEvent(new LifecycleEvents.TaskReleased(
                taskKey, Instant.now(), "SYSTEM"
        ));

        log.info("Released task {}", taskKey.value());
    }

    @Override
    public void releaseTasks(List<TaskKey> taskKeys) {
        taskKeys.forEach(this::releaseTask);
    }

    // ==================== Task Assignment ====================

    @Override
    public void assignTask(TaskKey taskKey, UserKey userId, DeviceKey deviceId) {
        log.info("Assigning task {} to user {} with device {}",
                taskKey.value(), userId.value(), deviceId != null ? deviceId.value() : "none");

        Task task = getTask(taskKey);
        validateStateTransition(task, TaskStatus.ASSIGNED);

        AssignmentKey assignmentKey = new AssignmentKey(UUID.randomUUID().toString());

        TaskAssignment assignment = TaskAssignment.builder()
                .assignmentKey(assignmentKey)
                .taskKey(taskKey)
                .userId(userId)
                .deviceId(deviceId)
                .assignedAt(Instant.now())
                .active(true)
                .build();

        assignmentRepository.save(assignment);

        task.assign(userId, deviceId, assignmentKey);
        taskRepository.save(task);

        recordHistory(taskKey, task.getStatus(), TaskStatus.ASSIGNED, "TASK_ASSIGNED", userId.value());

        eventPublisher.publishEvent(new LifecycleEvents.TaskAssigned(
                taskKey, assignmentKey, userId, deviceId, Instant.now(), "SYSTEM"
        ));

        log.info("Assigned task {} to user {}", taskKey.value(), userId.value());
    }

    @Override
    public void unassignTask(TaskKey taskKey, String reason) {
        log.info("Unassigning task {} - reason: {}", taskKey.value(), reason);

        Task task = getTask(taskKey);
        AssignmentKey assignmentKey = task.getAssignmentKey();
        UserKey previousUserId = task.getAssignedUserId();

        if (assignmentKey != null) {
            assignmentRepository.findByAssignmentKey(assignmentKey)
                    .ifPresent(assignment -> {
                        assignment.deactivate();
                        assignmentRepository.save(assignment);
                    });
        }

        task.unassign();
        taskRepository.save(task);

        recordHistory(taskKey, TaskStatus.ASSIGNED, TaskStatus.RELEASED, "TASK_UNASSIGNED", "SYSTEM");

        eventPublisher.publishEvent(new LifecycleEvents.TaskUnassigned(
                taskKey, assignmentKey, previousUserId, reason, Instant.now(), "SYSTEM"
        ));

        log.info("Unassigned task {}", taskKey.value());
    }

    @Override
    public void reassignTask(TaskKey taskKey, UserKey newUserId) {
        log.info("Reassigning task {} to user {}", taskKey.value(), newUserId.value());

        Task task = getTask(taskKey);
        UserKey fromUserId = task.getAssignedUserId();
        DeviceKey deviceId = task.getDeviceId();

        unassignTask(taskKey, "Reassignment");
        assignTask(taskKey, newUserId, deviceId);

        eventPublisher.publishEvent(new LifecycleEvents.TaskReassigned(
                taskKey, fromUserId, newUserId, "Reassignment", Instant.now(), "SYSTEM"
        ));

        log.info("Reassigned task {} from {} to {}", taskKey.value(),
                fromUserId != null ? fromUserId.value() : "unassigned", newUserId.value());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Task> getNextTaskForUser(UserKey userId, ZoneKey zone) {
        return workQueueRepository.findActiveQueuesByZone(zone).stream()
                .flatMap(queue -> taskRepository.findByQueueKeyAndStatus(queue.getQueueKey(), TaskStatus.RELEASED).stream())
                .sorted(Comparator.comparing(t -> t.getPriority().score(), Comparator.reverseOrder()))
                .findFirst();
    }

    // ==================== Task Execution ====================

    @Override
    public void startTask(TaskKey taskKey) {
        log.info("Starting task {}", taskKey.value());

        Task task = getTask(taskKey);
        validateStateTransition(task, TaskStatus.IN_PROGRESS);

        task.start();
        taskRepository.save(task);

        recordHistory(taskKey, TaskStatus.ASSIGNED, TaskStatus.IN_PROGRESS, "TASK_STARTED",
                task.getAssignedUserId() != null ? task.getAssignedUserId().value() : "SYSTEM");

        eventPublisher.publishEvent(new LifecycleEvents.TaskStarted(
                taskKey, task.getAssignedUserId(), task.getDeviceId(),
                task.getFromLocation(), Instant.now()
        ));

        log.info("Started task {}", taskKey.value());
    }

    @Override
    public void suspendTask(TaskKey taskKey, String reason) {
        log.info("Suspending task {} - reason: {}", taskKey.value(), reason);

        Task task = getTask(taskKey);
        validateStateTransition(task, TaskStatus.SUSPENDED);

        task.suspend(reason);
        taskRepository.save(task);

        recordHistory(taskKey, TaskStatus.IN_PROGRESS, TaskStatus.SUSPENDED, "TASK_SUSPENDED", "SYSTEM");

        eventPublisher.publishEvent(new LifecycleEvents.TaskSuspended(
                taskKey, reason, Instant.now(), "SYSTEM"
        ));

        log.info("Suspended task {}", taskKey.value());
    }

    @Override
    public void resumeTask(TaskKey taskKey) {
        log.info("Resuming task {}", taskKey.value());

        Task task = getTask(taskKey);
        validateStateTransition(task, TaskStatus.IN_PROGRESS);

        task.resume();
        taskRepository.save(task);

        recordHistory(taskKey, TaskStatus.SUSPENDED, TaskStatus.IN_PROGRESS, "TASK_RESUMED", "SYSTEM");

        eventPublisher.publishEvent(new LifecycleEvents.TaskResumed(
                taskKey, Instant.now(), "SYSTEM"
        ));

        log.info("Resumed task {}", taskKey.value());
    }

    @Override
    public void completeTask(TaskKey taskKey, BigDecimal actualQuantity) {
        log.info("Completing task {} with quantity {}", taskKey.value(), actualQuantity);

        Task task = getTask(taskKey);
        validateStateTransition(task, TaskStatus.COMPLETED);

        task.complete(actualQuantity);
        taskRepository.save(task);

        if (task.getAssignmentKey() != null) {
            assignmentRepository.findByAssignmentKey(task.getAssignmentKey())
                    .ifPresent(assignment -> {
                        assignment.complete();
                        assignmentRepository.save(assignment);
                    });
        }

        recordHistory(taskKey, TaskStatus.IN_PROGRESS, TaskStatus.COMPLETED, "TASK_COMPLETED",
                task.getAssignedUserId() != null ? task.getAssignedUserId().value() : "SYSTEM");

        eventPublisher.publishEvent(new LifecycleEvents.TaskCompleted(
                taskKey, task.getAssignedUserId(), task.getToLocation(),
                Map.of("actualQuantity", actualQuantity), Instant.now()
        ));

        log.info("Completed task {}", taskKey.value());
    }

    @Override
    public void cancelTask(TaskKey taskKey, String reason) {
        log.info("Cancelling task {} - reason: {}", taskKey.value(), reason);

        Task task = getTask(taskKey);
        validateStateTransition(task, TaskStatus.CANCELLED);

        task.cancel(reason);
        taskRepository.save(task);

        if (task.getAssignmentKey() != null) {
            assignmentRepository.findByAssignmentKey(task.getAssignmentKey())
                    .ifPresent(assignment -> {
                        assignment.deactivate();
                        assignmentRepository.save(assignment);
                    });
        }

        recordHistory(taskKey, task.getStatus(), TaskStatus.CANCELLED, "TASK_CANCELLED", "SYSTEM");

        eventPublisher.publishEvent(new LifecycleEvents.TaskCancelled(
                taskKey, reason, Instant.now(), "SYSTEM"
        ));

        log.info("Cancelled task {}", taskKey.value());
    }

    @Override
    public void closeTask(TaskKey taskKey) {
        log.info("Closing task {}", taskKey.value());

        Task task = getTask(taskKey);
        validateStateTransition(task, TaskStatus.CLOSED);

        task.close();
        taskRepository.save(task);

        recordHistory(taskKey, task.getStatus(), TaskStatus.CLOSED, "TASK_CLOSED", "SYSTEM");

        eventPublisher.publishEvent(new LifecycleEvents.TaskClosed(
                taskKey, Instant.now(), "SYSTEM"
        ));

        log.info("Closed task {}", taskKey.value());
    }

    // ==================== Bulk Operations ====================

    @Override
    public void closeTasks(List<TaskKey> taskKeys) {
        taskKeys.forEach(this::closeTask);
    }

    @Override
    public void cancelTasks(List<TaskKey> taskKeys, String reason) {
        taskKeys.forEach(key -> cancelTask(key, reason));
    }

    // ==================== Task History ====================

    @Override
    @Transactional(readOnly = true)
    public List<TaskHistory> getTaskHistory(TaskKey taskKey) {
        return historyRepository.findByTaskKey(taskKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskHistory> getTaskHistoryByUser(UserKey userId, LocalDateTime from, LocalDateTime to) {
        return historyRepository.findByUserIdAndDateRange(userId, from, to);
    }

    // ==================== Metrics ====================

    @Override
    @Transactional(readOnly = true)
    public int countTasksByStatus(TaskStatus status) {
        return taskRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public int countTasksByZoneAndStatus(ZoneKey zone, TaskStatus status) {
        return taskRepository.countByZoneAndStatus(zone, status);
    }

    // ==================== Private Helpers ====================

    private void validateStateTransition(Task task, TaskStatus targetStatus) {
        if (!task.canTransitionTo(targetStatus)) {
            throw new InvalidTaskStateException(
                    "Cannot transition task " + task.getTaskKey().value() +
                    " from " + task.getStatus() + " to " + targetStatus
            );
        }
    }

    private void recordHistory(TaskKey taskKey, TaskStatus previousStatus, TaskStatus newStatus,
                               String action, String performedBy) {
        HistoryKey historyKey = new HistoryKey(UUID.randomUUID().toString());

        TaskHistory history = TaskHistory.builder()
                .historyKey(historyKey)
                .taskKey(taskKey)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .action(action)
                .performedBy(performedBy)
                .performedAt(Instant.now())
                .build();

        historyRepository.save(history);

        eventPublisher.publishEvent(new LifecycleEvents.TaskHistoryRecorded(
                taskKey, historyKey,
                previousStatus != null ? previousStatus.name() : null,
                newStatus.name(), action, performedBy, Instant.now()
        ));
    }
}
