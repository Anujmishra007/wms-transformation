package com.maersk.wms.inbound.domain.operations_service.repository;

import com.maersk.wms.inbound.domain.operations_service.PutawayTask;
import com.maersk.wms.inbound.domain.operations_service.PutawayTaskStatus;
import com.maersk.wms.inbound.domain.operations_service.PutawayTaskType;
import com.maersk.wms.inbound.shared.kernel.identifiers.LocationKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.LpnKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.ReceiptKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for PutawayTask aggregate in Operations subdomain.
 * Part of inbound-operations-service.
 */
public interface PutawayTaskRepository {

    Optional<PutawayTask> findByKey(String putawayKey);

    List<PutawayTask> findByReceiptKey(ReceiptKey receiptKey);

    List<PutawayTask> findByStorerKey(StorerKey storerKey);

    List<PutawayTask> findByStatus(PutawayTaskStatus status);

    List<PutawayTask> findByTaskType(PutawayTaskType taskType);

    List<PutawayTask> findByStorerAndStatus(StorerKey storerKey, PutawayTaskStatus status);

    List<PutawayTask> findBySourceLpn(LpnKey lpnKey);

    List<PutawayTask> findByFromLocation(LocationKey locationKey);

    List<PutawayTask> findByToLocation(LocationKey locationKey);

    List<PutawayTask> findByAssignedUser(String userId);

    List<PutawayTask> findByZone(String putawayZone);

    List<PutawayTask> findPendingByPriority();

    List<PutawayTask> findCrossdockTasks();

    List<PutawayTask> findByDateRange(LocalDate from, LocalDate to);

    List<PutawayTask> findUnassigned();

    List<PutawayTask> findByWaveKey(String waveKey);

    List<PutawayTask> findByOrderKey(String orderKey);

    PutawayTask save(PutawayTask putawayTask);

    void delete(String putawayKey);

    boolean exists(String putawayKey);

    long countByStatus(PutawayTaskStatus status);

    long countByStorerAndStatus(StorerKey storerKey, PutawayTaskStatus status);

    long countByZoneAndStatus(String zone, PutawayTaskStatus status);
}
