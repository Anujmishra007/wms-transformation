package com.maersk.wms.picking.domain.progression_service.repository;

import com.maersk.wms.picking.domain.progression_service.model.PickDetailUpdate;
import com.maersk.wms.picking.domain.progression_service.model.ProgressionEventType;
import com.maersk.wms.picking.shared.kernel.identifiers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for PickDetailUpdate audit records.
 */
public interface PickDetailUpdateRepository {

    // CRUD
    PickDetailUpdate save(PickDetailUpdate update);
    List<PickDetailUpdate> saveAll(List<PickDetailUpdate> updates);
    Optional<PickDetailUpdate> findById(String updateId);

    // Queries
    List<PickDetailUpdate> findByPickDetail(PickDetailKey pickDetailKey);
    List<PickDetailUpdate> findByPickDetailOrderByTimestamp(PickDetailKey pickDetailKey);
    List<PickDetailUpdate> findByOrder(OrderKey orderKey);
    List<PickDetailUpdate> findByWave(WaveKey waveKey);
    List<PickDetailUpdate> findByUser(UserKey userId);
    List<PickDetailUpdate> findByEventType(ProgressionEventType eventType);
    List<PickDetailUpdate> findByDateRange(LocalDateTime from, LocalDateTime to);
    List<PickDetailUpdate> findByUserAndDateRange(UserKey userId, LocalDateTime from, LocalDateTime to);
    List<PickDetailUpdate> findByEventTypeAndDateRange(ProgressionEventType eventType, LocalDateTime from, LocalDateTime to);

    // Latest
    Optional<PickDetailUpdate> findLatestByPickDetail(PickDetailKey pickDetailKey);

    // Counts
    int countByPickDetail(PickDetailKey pickDetailKey);
    int countByEventTypeAndDateRange(ProgressionEventType eventType, LocalDateTime from, LocalDateTime to);
}
