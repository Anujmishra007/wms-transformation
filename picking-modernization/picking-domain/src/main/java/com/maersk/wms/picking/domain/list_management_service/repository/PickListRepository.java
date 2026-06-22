package com.maersk.wms.picking.domain.list_management_service.repository;

import com.maersk.wms.picking.domain.list_management_service.model.PickList;
import com.maersk.wms.picking.domain.list_management_service.model.PickListStatus;
import com.maersk.wms.picking.domain.list_management_service.model.PickListType;
import com.maersk.wms.picking.shared.kernel.identifiers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for PickList aggregate.
 */
public interface PickListRepository {

    // CRUD
    PickList save(PickList list);
    List<PickList> saveAll(List<PickList> lists);
    Optional<PickList> findById(PickListKey listKey);
    void delete(PickListKey listKey);

    // Queries
    List<PickList> findByWave(WaveKey waveKey);
    List<PickList> findByZone(String zone);
    List<PickList> findByStatus(PickListStatus status);
    List<PickList> findByType(PickListType type);
    List<PickList> findByUser(UserKey userId);
    List<PickList> findByDevice(DeviceKey deviceId);
    List<PickList> findByZoneAndStatus(String zone, PickListStatus status);
    List<PickList> findByTypeAndStatus(PickListType type, PickListStatus status);
    List<PickList> findByDateRange(LocalDateTime from, LocalDateTime to);

    // Unassigned
    List<PickList> findUnassigned();
    List<PickList> findUnassignedByZone(String zone);
    List<PickList> findUnassignedByType(PickListType type);

    // Open Lists
    List<PickList> findOpenLists();
    List<PickList> findOpenListsByZone(String zone);

    // Counts
    int countByStatus(PickListStatus status);
    int countByZoneAndStatus(String zone, PickListStatus status);
    int countUnassigned();
    int countUnassignedByZone(String zone);

    // Contains Task
    Optional<PickList> findByContainingTask(PickTaskKey taskKey);

    // Exists
    boolean existsByWave(WaveKey waveKey);
}
