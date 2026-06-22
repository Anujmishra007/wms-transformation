package com.maersk.wms.task.domain.lifecycle_service.repository;

import com.maersk.wms.task.domain.lifecycle_service.model.TaskAssignment;
import com.maersk.wms.task.shared.kernel.identifiers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for TaskAssignment entity.
 */
public interface TaskAssignmentRepository {

    TaskAssignment save(TaskAssignment assignment);
    Optional<TaskAssignment> findById(AssignmentKey assignmentKey);

    List<TaskAssignment> findByTask(TaskKey taskKey);
    List<TaskAssignment> findByUser(UserKey userId);
    List<TaskAssignment> findByUserAndStatus(UserKey userId, TaskAssignment.AssignmentStatus status);
    List<TaskAssignment> findActiveByUser(UserKey userId);
    Optional<TaskAssignment> findCurrentAssignment(TaskKey taskKey);
    List<TaskAssignment> findByDateRange(LocalDateTime from, LocalDateTime to);

    int countActiveByUser(UserKey userId);
}
