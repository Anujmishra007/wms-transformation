package com.maersk.wms.task.domain.repository;

import com.maersk.wms.task.domain.entity.TaskAssignment;
import com.maersk.wms.task.domain.enums.AssignmentStatus;
import com.maersk.wms.task.domain.enums.AssignmentType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TaskAssignment entity operations.
 */
public interface TaskAssignmentRepository {

    TaskAssignment save(TaskAssignment assignment);

    Optional<TaskAssignment> findByAssignmentKey(Long assignmentKey);

    Optional<TaskAssignment> findByAssignmentId(String assignmentId);

    Optional<TaskAssignment> findActiveAssignmentByTaskKey(Long taskKey);

    List<TaskAssignment> findByUserId(String userId);

    List<TaskAssignment> findByUserIdAndStatus(String userId, AssignmentStatus status);

    List<TaskAssignment> findByTaskKey(Long taskKey);

    List<TaskAssignment> findByTaskGroupKey(Long taskGroupKey);

    List<TaskAssignment> findByStatus(AssignmentStatus status);

    List<TaskAssignment> findByAssignmentType(AssignmentType assignmentType);

    List<TaskAssignment> findByWorkGroup(String workGroup);

    List<TaskAssignment> findByWorkZone(String workZone);

    List<TaskAssignment> findActiveAssignmentsByUserId(String userId);

    List<TaskAssignment> findPendingAssignmentsByUserId(String userId);

    List<TaskAssignment> findExpiredAssignments(LocalDateTime expirationTime);

    int countActiveAssignmentsByUserId(String userId);

    int countByUserIdAndStatus(String userId, AssignmentStatus status);

    void updateStatus(Long assignmentKey, AssignmentStatus status, String modifiedBy);

    void releaseAssignment(Long assignmentKey, String reason, String releasedBy);

    void completeAssignment(Long assignmentKey, String completionReason, String completedBy);

    void deleteByAssignmentKey(Long assignmentKey);
}
