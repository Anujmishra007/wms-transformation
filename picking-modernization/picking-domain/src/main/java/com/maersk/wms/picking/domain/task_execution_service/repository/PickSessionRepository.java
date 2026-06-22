package com.maersk.wms.picking.domain.task_execution_service.repository;

import com.maersk.wms.picking.domain.task_execution_service.model.PickSession;
import com.maersk.wms.picking.domain.task_execution_service.model.SessionStatus;
import com.maersk.wms.picking.shared.kernel.identifiers.DeviceKey;
import com.maersk.wms.picking.shared.kernel.identifiers.UserKey;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for PickSession aggregate.
 */
public interface PickSessionRepository {

    // CRUD
    PickSession save(PickSession session);
    Optional<PickSession> findById(String sessionId);
    void delete(String sessionId);

    // Queries
    Optional<PickSession> findActiveByUser(UserKey userId);
    Optional<PickSession> findActiveByDevice(DeviceKey deviceId);
    List<PickSession> findByStatus(SessionStatus status);
    List<PickSession> findByZone(String zone);
    List<PickSession> findByDateRange(LocalDateTime from, LocalDateTime to);

    // Counts
    int countActiveSessions();
    int countActiveSessionsByZone(String zone);

    // Cleanup
    List<PickSession> findStaleSessions(LocalDateTime threshold);
}
