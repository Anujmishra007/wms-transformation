package com.maersk.wms.picking.domain.cancellation_service.service;

import com.maersk.wms.picking.domain.cancellation_service.model.CancellationApprovalStatus;
import com.maersk.wms.picking.domain.cancellation_service.model.CancellationReasonCode;
import com.maersk.wms.picking.domain.cancellation_service.model.CancellationScope;
import com.maersk.wms.picking.domain.cancellation_service.model.PickCancellation;
import com.maersk.wms.picking.shared.kernel.identifiers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Cancellation Service - handles pick task and order cancellations.
 * Manages cancellation requests, approvals, and inventory deallocations.
 */
public interface CancellationService {

    // Cancellation Requests
    PickCancellation requestTaskCancellation(PickTaskKey taskKey, CancellationReasonCode reason, String notes);
    PickCancellation requestDetailCancellation(PickDetailKey pickDetailKey, CancellationReasonCode reason, String notes);
    PickCancellation requestOrderCancellation(OrderKey orderKey, CancellationReasonCode reason, String notes);
    PickCancellation requestWaveCancellation(WaveKey waveKey, CancellationReasonCode reason, String notes);
    PickCancellation requestListCancellation(PickListKey listKey, CancellationReasonCode reason, String notes);

    // Approval Workflow
    void submitForApproval(String cancellationId);
    void approveCancellation(String cancellationId, UserKey approvedBy, String notes);
    void rejectCancellation(String cancellationId, UserKey rejectedBy, String rejectionReason);
    boolean requiresApproval(CancellationReasonCode reason, CancellationScope scope);

    // Cancellation Execution
    void executeCancellation(String cancellationId);
    void executeImmediateCancellation(PickTaskKey taskKey, CancellationReasonCode reason, UserKey cancelledBy);
    void executeBatchCancellation(List<PickTaskKey> taskKeys, CancellationReasonCode reason, UserKey cancelledBy);

    // Deallocation
    void deallocateInventory(String cancellationId);
    void returnToAvailable(PickDetailKey pickDetailKey);
    void triggerReallocation(String cancellationId, boolean sameOrder);

    // Query
    Optional<PickCancellation> getCancellation(String cancellationId);
    List<PickCancellation> getCancellationsByOrder(OrderKey orderKey);
    List<PickCancellation> getCancellationsByWave(WaveKey waveKey);
    List<PickCancellation> getPendingApprovals(UserKey approverId);
    List<PickCancellation> getCancellationsByStatus(CancellationApprovalStatus status);
    List<PickCancellation> getCancellationHistory(LocalDateTime from, LocalDateTime to);

    // Validation
    boolean canCancelTask(PickTaskKey taskKey);
    boolean canCancelOrder(OrderKey orderKey);
    String getCancellationBlockReason(PickTaskKey taskKey);

    // Rollback
    void rollbackCancellation(String cancellationId, String reason);
    boolean canRollback(String cancellationId);
}
