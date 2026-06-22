package com.maersk.wms.task.domain.repository;

import com.maersk.wms.task.domain.entity.Task;
import com.maersk.wms.task.domain.enums.TaskStatus;
import com.maersk.wms.task.domain.enums.TaskType;
import com.maersk.wms.task.domain.enums.TaskPriority;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Task entity operations.
 */
public interface TaskRepository {

    Task save(Task task);

    Optional<Task> findByTaskKey(Long taskKey);

    Optional<Task> findByTaskId(String taskId);

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByTaskType(TaskType taskType);

    List<Task> findByAssignedUserId(String userId);

    List<Task> findByStatusAndTaskType(TaskStatus status, TaskType taskType);

    List<Task> findByWorkGroup(String workGroup);

    List<Task> findByWorkZone(String workZone);

    List<Task> findBySourceLocation(String sourceLocation);

    List<Task> findByDestinationLocation(String destinationLocation);

    List<Task> findByOrderKey(Long orderKey);

    List<Task> findByWaveId(String waveId);

    List<Task> findByRouteId(String routeId);

    List<Task> findPendingTasksByPriority(TaskPriority minPriority);

    List<Task> findUnassignedTasks();

    List<Task> findUnassignedTasksByWorkZone(String workZone);

    List<Task> findOverdueTasks(LocalDateTime dueDate);

    List<Task> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    int countByStatus(TaskStatus status);

    int countByStatusAndWorkGroup(TaskStatus status, String workGroup);

    int countByAssignedUserId(String userId);

    void updateStatus(Long taskKey, TaskStatus status, String modifiedBy);

    void assignTask(Long taskKey, String userId, String userName);

    void unassignTask(Long taskKey, String reason);

    void completeTask(Long taskKey, Double completedQuantity, String completedBy);

    void deleteByTaskKey(Long taskKey);

    boolean existsByTaskId(String taskId);
}
