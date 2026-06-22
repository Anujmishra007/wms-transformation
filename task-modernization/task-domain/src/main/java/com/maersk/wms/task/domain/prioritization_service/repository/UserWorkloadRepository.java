package com.maersk.wms.task.domain.prioritization_service.repository;

import com.maersk.wms.task.domain.prioritization_service.model.UserWorkload;
import com.maersk.wms.task.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Repository for UserWorkload entity.
 */
public interface UserWorkloadRepository {

    UserWorkload save(UserWorkload workload);
    Optional<UserWorkload> findById(UserKey userId);
    void delete(UserKey userId);

    List<UserWorkload> findByStatus(UserWorkload.WorkloadStatus status);
    List<UserWorkload> findByZone(ZoneKey zone);
    List<UserWorkload> findAvailableInZone(ZoneKey zone);
    List<UserWorkload> findWithCapacity(ZoneKey zone);
    List<UserWorkload> findAll();

    int countByStatus(UserWorkload.WorkloadStatus status);
    int countAvailableInZone(ZoneKey zone);
}
