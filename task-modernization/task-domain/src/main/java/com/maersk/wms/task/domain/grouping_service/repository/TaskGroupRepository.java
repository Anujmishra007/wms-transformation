package com.maersk.wms.task.domain.grouping_service.repository;

import com.maersk.wms.task.domain.grouping_service.model.*;
import com.maersk.wms.task.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Repository for TaskGroup aggregate.
 */
public interface TaskGroupRepository {

    TaskGroup save(TaskGroup group);
    List<TaskGroup> saveAll(List<TaskGroup> groups);
    Optional<TaskGroup> findById(TaskGroupKey groupKey);
    void delete(TaskGroupKey groupKey);

    List<TaskGroup> findByStatus(TaskGroupStatus status);
    List<TaskGroup> findByType(TaskGroupType type);
    List<TaskGroup> findByTypeAndStatus(TaskGroupType type, TaskGroupStatus status);
    List<TaskGroup> findByWave(WaveKey waveKey);
    List<TaskGroup> findByZone(ZoneKey zone);
    List<TaskGroup> findByUser(UserKey userId);

    int countByStatus(TaskGroupStatus status);
    int countByTypeAndStatus(TaskGroupType type, TaskGroupStatus status);
}
