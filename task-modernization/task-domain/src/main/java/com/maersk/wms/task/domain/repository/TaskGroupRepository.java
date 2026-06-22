package com.maersk.wms.task.domain.repository;

import com.maersk.wms.task.domain.entity.TaskGroup;
import com.maersk.wms.task.domain.enums.TaskGroupStatus;
import com.maersk.wms.task.domain.enums.TaskGroupType;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TaskGroup entity operations.
 */
public interface TaskGroupRepository {

    TaskGroup save(TaskGroup taskGroup);

    Optional<TaskGroup> findByTaskGroupKey(Long taskGroupKey);

    Optional<TaskGroup> findByTaskGroupId(String taskGroupId);

    List<TaskGroup> findByStatus(TaskGroupStatus status);

    List<TaskGroup> findByGroupType(TaskGroupType groupType);

    List<TaskGroup> findByAssignedUserId(String userId);

    List<TaskGroup> findByWaveId(String waveId);

    List<TaskGroup> findByRouteId(String routeId);

    List<TaskGroup> findByWorkGroup(String workGroup);

    List<TaskGroup> findActiveGroups();

    List<TaskGroup> findByStatusAndGroupType(TaskGroupStatus status, TaskGroupType groupType);

    int countByStatus(TaskGroupStatus status);

    void updateStatus(Long taskGroupKey, TaskGroupStatus status, String modifiedBy);

    void updateTaskCounts(Long taskGroupKey, int totalTasks, int completedTasks, int pendingTasks, int inProgressTasks);

    void deleteByTaskGroupKey(Long taskGroupKey);

    boolean existsByTaskGroupId(String taskGroupId);
}
