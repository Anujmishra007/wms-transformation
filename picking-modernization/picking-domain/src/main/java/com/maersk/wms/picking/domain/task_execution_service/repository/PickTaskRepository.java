package com.maersk.wms.picking.domain.task_execution_service.repository;

import com.maersk.wms.picking.domain.task_execution_service.model.PickTask;
import com.maersk.wms.picking.domain.task_execution_service.model.PickTaskStatus;
import com.maersk.wms.picking.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PickTask aggregate.
 */
public interface PickTaskRepository {

    // CRUD
    PickTask save(PickTask task);
    List<PickTask> saveAll(List<PickTask> tasks);
    Optional<PickTask> findById(PickTaskKey taskKey);
    void delete(PickTaskKey taskKey);

    // Queries
    List<PickTask> findByPickDetail(PickDetailKey pickDetailKey);
    List<PickTask> findByPickList(PickListKey listKey);
    List<PickTask> findByOrder(OrderKey orderKey);
    List<PickTask> findByWave(WaveKey waveKey);
    List<PickTask> findByUser(UserKey userId);
    List<PickTask> findByStatus(PickTaskStatus status);
    List<PickTask> findByZoneAndStatus(String zone, PickTaskStatus status);
    List<PickTask> findByZoneAndStatusOrderByPriority(String zone, PickTaskStatus status);

    // Counts
    int countByStatus(PickTaskStatus status);
    int countByZoneAndStatus(String zone, PickTaskStatus status);
    int countByUser(UserKey userId);

    // Next Task
    Optional<PickTask> findNextAvailableTask(String zone, String equipmentType);
    Optional<PickTask> findNextTaskForUser(UserKey userId);

    // Exists
    boolean existsByPickDetail(PickDetailKey pickDetailKey);
}
