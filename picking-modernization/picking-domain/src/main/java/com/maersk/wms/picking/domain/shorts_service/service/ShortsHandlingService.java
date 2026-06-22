package com.maersk.wms.picking.domain.shorts_service.service;

import com.maersk.wms.picking.domain.shorts_service.model.ShortPickRecord;
import com.maersk.wms.picking.domain.shorts_service.model.ShortReasonCode;
import com.maersk.wms.picking.domain.shorts_service.model.ShortResolutionAction;
import com.maersk.wms.picking.domain.shorts_service.model.ShortResolutionStatus;
import com.maersk.wms.picking.shared.kernel.identifiers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Shorts Handling Service - manages short pick scenarios and resolution.
 * Handles inventory shortage recording, resolution tracking, and reallocation triggers.
 */
public interface ShortsHandlingService {

    // Short Recording
    ShortPickRecord recordShort(PickDetailKey pickDetailKey, BigDecimal shortedQty, ShortReasonCode reasonCode, String notes);
    ShortPickRecord recordPartialPick(PickDetailKey pickDetailKey, BigDecimal pickedQty, BigDecimal shortedQty, ShortReasonCode reasonCode);
    ShortPickRecord recordFullShort(PickDetailKey pickDetailKey, ShortReasonCode reasonCode, String notes);

    // Verification
    boolean verifyShort(String shortRecordId, UserKey verifiedBy, boolean confirmed, String notes);
    void requestSupervisorOverride(String shortRecordId, String reason);
    void applySupervisorOverride(String shortRecordId, UserKey supervisorId, boolean approved, String notes);

    // Resolution
    void resolveShort(String shortRecordId, ShortResolutionAction action, String notes);
    void triggerReallocation(String shortRecordId);
    void triggerReplenishment(String shortRecordId, LocationKey location);
    void closeWithBackorder(String shortRecordId, String backorderReference);
    void cancelShortedLine(String shortRecordId, String reason);

    // Query
    Optional<ShortPickRecord> getShortRecord(String shortRecordId);
    List<ShortPickRecord> getShortsByPickDetail(PickDetailKey pickDetailKey);
    List<ShortPickRecord> getShortsByOrder(OrderKey orderKey);
    List<ShortPickRecord> getShortsByLocation(LocationKey location);
    List<ShortPickRecord> getShortsByStatus(ShortResolutionStatus status);
    List<ShortPickRecord> getUnresolvedShorts(String zone, LocalDateTime since);

    // Analytics
    int countShortsToday(String zone);
    BigDecimal getTotalShortedQuantity(LocalDateTime from, LocalDateTime to);
    List<LocationKey> getHighShortLocations(int threshold);
    List<SkuKey> getHighShortSkus(int threshold);

    // Notifications
    void notifySupervisor(String shortRecordId, String urgency);
    void notifyInventoryTeam(String shortRecordId);
}
