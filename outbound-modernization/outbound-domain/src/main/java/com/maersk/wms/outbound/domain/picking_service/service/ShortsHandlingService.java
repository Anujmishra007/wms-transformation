package com.maersk.wms.outbound.domain.picking_service.service;

import com.maersk.wms.outbound.domain.picking_service.model.ShortPickRecord;
import com.maersk.wms.outbound.domain.picking_service.model.ShortReasonCode;
import com.maersk.wms.outbound.domain.picking_service.model.ShortResolutionStatus;
import com.maersk.wms.outbound.shared.kernel.identifiers.PickDetailKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.StorerKey;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for Shorts Handling.
 * Manages short pick processing.
 * Part of Picking Operations Service bounded context.
 */
public interface ShortsHandlingService {

    /**
     * Records a short pick.
     */
    ShortPickRecord recordShortPick(PickDetailKey pickDetailKey, RecordShortCommand command);

    /**
     * Resolves a short pick by reallocating.
     */
    ShortPickRecord reallocateShort(String shortPickId, String userId);

    /**
     * Resolves a short pick by backordering.
     */
    ShortPickRecord backorderShort(String shortPickId, String userId);

    /**
     * Cancels a short pick resolution.
     */
    ShortPickRecord cancelShort(String shortPickId, String cancelReason, String userId);

    /**
     * Gets all pending short picks.
     */
    List<ShortPickRecord> getPendingShorts(StorerKey storerKey);

    /**
     * Gets short pick by ID.
     */
    ShortPickRecord getShortPick(String shortPickId);

    /**
     * Gets short pick history for a date range.
     */
    List<ShortPickRecord> getShortHistory(StorerKey storerKey, LocalDate fromDate, LocalDate toDate);

    /**
     * Gets short pick statistics.
     */
    ShortStatistics getShortStatistics(StorerKey storerKey, LocalDate date);

    /**
     * Command to record a short.
     */
    record RecordShortCommand(
            String userId,
            String deviceId,
            BigDecimal expectedQty,
            BigDecimal actualQty,
            ShortReasonCode reasonCode,
            String reasonDescription
    ) {}

    /**
     * Short pick statistics.
     */
    record ShortStatistics(
            int totalShorts,
            int pendingShorts,
            int resolvedShorts,
            BigDecimal totalShortQty,
            java.util.Map<ShortReasonCode, Integer> shortsByReason
    ) {}
}
