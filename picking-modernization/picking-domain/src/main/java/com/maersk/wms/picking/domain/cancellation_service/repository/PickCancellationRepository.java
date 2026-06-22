package com.maersk.wms.picking.domain.cancellation_service.repository;

import com.maersk.wms.picking.domain.cancellation_service.model.CancellationApprovalStatus;
import com.maersk.wms.picking.domain.cancellation_service.model.CancellationReasonCode;
import com.maersk.wms.picking.domain.cancellation_service.model.CancellationScope;
import com.maersk.wms.picking.domain.cancellation_service.model.PickCancellation;
import com.maersk.wms.picking.shared.kernel.identifiers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for PickCancellation aggregate.
 */
public interface PickCancellationRepository {

    // CRUD
    PickCancellation save(PickCancellation cancellation);
    List<PickCancellation> saveAll(List<PickCancellation> cancellations);
    Optional<PickCancellation> findById(String cancellationId);
    void delete(String cancellationId);

    // Queries
    List<PickCancellation> findByTask(PickTaskKey taskKey);
    List<PickCancellation> findByPickDetail(PickDetailKey pickDetailKey);
    List<PickCancellation> findByOrder(OrderKey orderKey);
    List<PickCancellation> findByWave(WaveKey waveKey);
    List<PickCancellation> findByPickList(PickListKey listKey);
    List<PickCancellation> findByScope(CancellationScope scope);
    List<PickCancellation> findByReasonCode(CancellationReasonCode reasonCode);
    List<PickCancellation> findByApprovalStatus(CancellationApprovalStatus status);
    List<PickCancellation> findByRequestedBy(UserKey userId);
    List<PickCancellation> findByDateRange(LocalDateTime from, LocalDateTime to);

    // Approval Workflow
    List<PickCancellation> findPendingApproval();
    List<PickCancellation> findPendingApprovalForUser(UserKey approverId);

    // Counts
    int countByApprovalStatus(CancellationApprovalStatus status);
    int countByDateRange(LocalDateTime from, LocalDateTime to);
    int countByReasonCodeAndDateRange(CancellationReasonCode reasonCode, LocalDateTime from, LocalDateTime to);

    // Exists
    boolean existsPendingForTask(PickTaskKey taskKey);
    boolean existsPendingForOrder(OrderKey orderKey);
}
