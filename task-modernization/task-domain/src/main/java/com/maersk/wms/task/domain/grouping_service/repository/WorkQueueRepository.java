package com.maersk.wms.task.domain.grouping_service.repository;

import com.maersk.wms.task.domain.grouping_service.model.WorkQueue;
import com.maersk.wms.task.domain.lifecycle_service.model.TaskType;
import com.maersk.wms.task.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Repository for WorkQueue aggregate.
 */
public interface WorkQueueRepository {

    WorkQueue save(WorkQueue queue);
    Optional<WorkQueue> findById(WorkQueueKey queueKey);
    void delete(WorkQueueKey queueKey);

    List<WorkQueue> findByStatus(WorkQueue.WorkQueueStatus status);
    List<WorkQueue> findByZone(ZoneKey zone);
    List<WorkQueue> findByTaskType(TaskType taskType);
    List<WorkQueue> findByZoneAndStatus(ZoneKey zone, WorkQueue.WorkQueueStatus status);

    List<WorkQueue> findActive();
    Optional<WorkQueue> findByNameAndZone(String name, ZoneKey zone);

    int countByStatus(WorkQueue.WorkQueueStatus status);
}
