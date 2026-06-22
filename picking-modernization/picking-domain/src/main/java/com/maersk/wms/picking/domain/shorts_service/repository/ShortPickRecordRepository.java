package com.maersk.wms.picking.domain.shorts_service.repository;

import com.maersk.wms.picking.domain.shorts_service.model.ShortPickRecord;
import com.maersk.wms.picking.domain.shorts_service.model.ShortReasonCode;
import com.maersk.wms.picking.domain.shorts_service.model.ShortResolutionStatus;
import com.maersk.wms.picking.shared.kernel.identifiers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for ShortPickRecord aggregate.
 */
public interface ShortPickRecordRepository {

    // CRUD
    ShortPickRecord save(ShortPickRecord record);
    List<ShortPickRecord> saveAll(List<ShortPickRecord> records);
    Optional<ShortPickRecord> findById(String shortRecordId);
    void delete(String shortRecordId);

    // Queries
    List<ShortPickRecord> findByPickDetail(PickDetailKey pickDetailKey);
    List<ShortPickRecord> findByOrder(OrderKey orderKey);
    List<ShortPickRecord> findByWave(WaveKey waveKey);
    List<ShortPickRecord> findByLocation(LocationKey location);
    List<ShortPickRecord> findBySku(SkuKey sku);
    List<ShortPickRecord> findByUser(UserKey userId);
    List<ShortPickRecord> findByReasonCode(ShortReasonCode reasonCode);
    List<ShortPickRecord> findByResolutionStatus(ShortResolutionStatus status);
    List<ShortPickRecord> findByZoneAndStatus(String zone, ShortResolutionStatus status);
    List<ShortPickRecord> findUnresolvedByZone(String zone);
    List<ShortPickRecord> findByDateRange(LocalDateTime from, LocalDateTime to);

    // Analytics
    int countByDateRange(LocalDateTime from, LocalDateTime to);
    int countByZoneAndDateRange(String zone, LocalDateTime from, LocalDateTime to);
    BigDecimal sumShortedQuantityByDateRange(LocalDateTime from, LocalDateTime to);
    List<LocationKey> findTopShortLocations(int limit, LocalDateTime from, LocalDateTime to);
    List<SkuKey> findTopShortSkus(int limit, LocalDateTime from, LocalDateTime to);

    // Pending
    List<ShortPickRecord> findPendingVerification();
    List<ShortPickRecord> findPendingSupervisorApproval();
}
