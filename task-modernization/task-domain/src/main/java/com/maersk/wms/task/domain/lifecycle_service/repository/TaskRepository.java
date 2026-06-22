package com.maersk.wms.task.domain.lifecycle_service.repository;

import com.maersk.wms.task.domain.lifecycle_service.model.*;
import com.maersk.wms.task.shared.kernel.identifiers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Task aggregate.
 */
public interface TaskRepository {

    Task save(Task task);
    List<Task> saveAll(List<Task> tasks);
    Optional<Task> findById(TaskKey taskKey);
    void delete(TaskKey taskKey);

    List<Task> findByStatus(TaskStatus status);
    List<Task> findByType(TaskType type);
    List<Task> findByTypeAndStatus(TaskType type, TaskStatus status);
    List<Task> findByZone(ZoneKey zone);
    List<Task> findByZoneAndStatus(ZoneKey zone, TaskStatus status);
    List<Task> findByGroup(TaskGroupKey groupKey);
    List<Task> findByQueue(WorkQueueKey queueKey);
    List<Task> findByAssignedUser(UserKey userId);
    List<Task> findBySourceTypeAndKey(String sourceType, String sourceKey);
    List<Task> findByDateRange(LocalDateTime from, LocalDateTime to);
    List<Task> findOverdue(LocalDateTime threshold);

    // Assignment queries
    Optional<Task> findNextAvailable(ZoneKey zone, TaskType type);
    Optional<Task> findNextAvailableForUser(UserKey userId, ZoneKey zone);

    // Counts
    int countByStatus(TaskStatus status);
    int countByZoneAndStatus(ZoneKey zone, TaskStatus status);
    int countByTypeAndStatus(TaskType type, TaskStatus status);
}
