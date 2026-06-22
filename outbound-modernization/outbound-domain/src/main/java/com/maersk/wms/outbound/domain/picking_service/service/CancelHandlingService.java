package com.maersk.wms.outbound.domain.picking_service.service;

import com.maersk.wms.outbound.domain.picking_service.model.CancellationReasonCode;
import com.maersk.wms.outbound.domain.picking_service.model.CancellationScope;
import com.maersk.wms.outbound.domain.picking_service.model.PickCancellation;
import com.maersk.wms.outbound.shared.kernel.identifiers.OrderKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.PickDetailKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.PickHeaderKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.WaveKey;

import java.util.List;

/**
 * Service interface for Cancel Handling.
 * Manages pick cancellation processing.
 * Part of Picking Operations Service bounded context.
 */
public interface CancelHandlingService {

    /**
     * Cancels a single pick detail.
     */
    PickCancellation cancelPickDetail(PickDetailKey pickDetailKey, CancelCommand command);

    /**
     * Cancels an entire pick header.
     */
    List<PickCancellation> cancelPickHeader(PickHeaderKey pickHeaderKey, CancelCommand command);

    /**
     * Cancels all picks for an order.
     */
    List<PickCancellation> cancelOrder(OrderKey orderKey, CancelCommand command);

    /**
     * Cancels all picks for a wave.
     */
    List<PickCancellation> cancelWave(WaveKey waveKey, CancelCommand command);

    /**
     * Approves a cancellation that required approval.
     */
    PickCancellation approveCancellation(String cancellationId, String approverUserId);

    /**
     * Rejects a cancellation request.
     */
    PickCancellation rejectCancellation(String cancellationId, String rejectReason, String approverUserId);

    /**
     * Gets cancellations pending approval.
     */
    List<PickCancellation> getPendingApprovals();

    /**
     * Gets cancellation by ID.
     */
    PickCancellation getCancellation(String cancellationId);

    /**
     * Validates if a cancellation is allowed.
     */
    CancellationValidation validateCancellation(PickDetailKey pickDetailKey, CancellationScope scope);

    /**
     * Command to cancel picks.
     */
    record CancelCommand(
            String userId,
            CancellationReasonCode reasonCode,
            String reasonDescription,
            boolean releaseInventory,
            boolean requireApproval
    ) {}

    /**
     * Validation result for cancellation.
     */
    record CancellationValidation(
            boolean canCancel,
            boolean requiresApproval,
            List<String> blockingReasons,
            List<String> warnings
    ) {}
}
