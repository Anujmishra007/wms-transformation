package com.maersk.wms.picking.service.impl;

import com.maersk.wms.picking.domain.shorts_service.model.ShortPickRecord;
import com.maersk.wms.picking.domain.shorts_service.model.ShortReasonCode;
import com.maersk.wms.picking.domain.shorts_service.model.ShortResolutionAction;
import com.maersk.wms.picking.domain.shorts_service.model.ShortResolutionStatus;
import com.maersk.wms.picking.domain.shorts_service.repository.ShortPickRecordRepository;
import com.maersk.wms.picking.domain.shorts_service.service.ShortsHandlingService;
import com.maersk.wms.picking.acl.allocation.AllocationFacade;
import com.maersk.wms.picking.acl.inventory.InventoryFacade;
import com.maersk.wms.picking.acl.replenishment.ReplenishmentFacade;
import com.maersk.wms.picking.shared.kernel.identifiers.*;
import com.maersk.wms.picking.shared.kernel.valueobjects.Quantity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of Shorts Handling Service.
 * Manages short pick scenarios and resolution.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShortsHandlingServiceImpl implements ShortsHandlingService {

    private final ShortPickRecordRepository shortRepository;
    private final AllocationFacade allocationFacade;
    private final InventoryFacade inventoryFacade;
    private final ReplenishmentFacade replenishmentFacade;

    // Short Recording

    @Override
    @Transactional
    public ShortPickRecord recordShort(PickDetailKey pickDetailKey, BigDecimal shortedQty,
                                       ShortReasonCode reasonCode, String notes) {
        log.info("Recording short for {} - qty: {}, reason: {}", pickDetailKey, shortedQty, reasonCode);

        ShortPickRecord record = ShortPickRecord.builder()
                .shortRecordId(UUID.randomUUID().toString())
                .pickDetailKey(pickDetailKey)
                .shortedQty(shortedQty)
                .reasonCode(reasonCode)
                .resolutionStatus(ShortResolutionStatus.PENDING)
                .notes(notes)
                .recordedTime(LocalDateTime.now())
                .requiresVerification(reasonCode.isRequiresVerification())
                .build();

        return shortRepository.save(record);
    }

    @Override
    @Transactional
    public ShortPickRecord recordPartialPick(PickDetailKey pickDetailKey, BigDecimal pickedQty,
                                             BigDecimal shortedQty, ShortReasonCode reasonCode) {
        log.info("Recording partial pick for {} - picked: {}, shorted: {}", pickDetailKey, pickedQty, shortedQty);

        ShortPickRecord record = ShortPickRecord.builder()
                .shortRecordId(UUID.randomUUID().toString())
                .pickDetailKey(pickDetailKey)
                .pickedQty(pickedQty)
                .shortedQty(shortedQty)
                .reasonCode(reasonCode)
                .resolutionStatus(ShortResolutionStatus.PENDING)
                .recordedTime(LocalDateTime.now())
                .requiresVerification(reasonCode.isRequiresVerification())
                .build();

        return shortRepository.save(record);
    }

    @Override
    @Transactional
    public ShortPickRecord recordFullShort(PickDetailKey pickDetailKey, ShortReasonCode reasonCode, String notes) {
        log.info("Recording full short for {} - reason: {}", pickDetailKey, reasonCode);

        ShortPickRecord record = ShortPickRecord.builder()
                .shortRecordId(UUID.randomUUID().toString())
                .pickDetailKey(pickDetailKey)
                .shortedQty(BigDecimal.ZERO) // Will be populated from pick detail
                .reasonCode(reasonCode)
                .resolutionStatus(ShortResolutionStatus.PENDING)
                .notes(notes)
                .recordedTime(LocalDateTime.now())
                .requiresVerification(true)
                .build();

        return shortRepository.save(record);
    }

    // Verification

    @Override
    @Transactional
    public boolean verifyShort(String shortRecordId, UserKey verifiedBy, boolean confirmed, String notes) {
        log.info("Verifying short {} - confirmed: {}", shortRecordId, confirmed);

        ShortPickRecord record = getShortRecord(shortRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Short record not found: " + shortRecordId));

        record.verify(verifiedBy, confirmed, notes);
        shortRepository.save(record);

        return confirmed;
    }

    @Override
    @Transactional
    public void requestSupervisorOverride(String shortRecordId, String reason) {
        log.info("Requesting supervisor override for short {}", shortRecordId);

        ShortPickRecord record = getShortRecord(shortRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Short record not found: " + shortRecordId));

        record.setResolutionStatus(ShortResolutionStatus.PENDING_APPROVAL);
        record.setNotes(record.getNotes() + " | Override requested: " + reason);
        shortRepository.save(record);
    }

    @Override
    @Transactional
    public void applySupervisorOverride(String shortRecordId, UserKey supervisorId,
                                        boolean approved, String notes) {
        log.info("Applying supervisor override for short {} - approved: {}", shortRecordId, approved);

        ShortPickRecord record = getShortRecord(shortRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Short record not found: " + shortRecordId));

        if (approved) {
            record.setResolutionStatus(ShortResolutionStatus.APPROVED);
        } else {
            record.setResolutionStatus(ShortResolutionStatus.REJECTED);
        }
        record.setApprovedBy(supervisorId);
        record.setApprovalTime(LocalDateTime.now());
        record.setNotes(record.getNotes() + " | Supervisor: " + notes);
        shortRepository.save(record);
    }

    // Resolution

    @Override
    @Transactional
    public void resolveShort(String shortRecordId, ShortResolutionAction action, String notes) {
        log.info("Resolving short {} with action {}", shortRecordId, action);

        ShortPickRecord record = getShortRecord(shortRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Short record not found: " + shortRecordId));

        record.resolve(action, notes);
        shortRepository.save(record);

        // Execute resolution action
        switch (action) {
            case REALLOCATE -> triggerReallocation(shortRecordId);
            case REPLENISH -> {
                // Would need location info
            }
            case BACKORDER -> closeWithBackorder(shortRecordId, null);
            case CANCEL -> cancelShortedLine(shortRecordId, notes);
            case MANUAL_FILL -> log.info("Manual fill selected for {}", shortRecordId);
        }
    }

    @Override
    @Transactional
    public void triggerReallocation(String shortRecordId) {
        log.info("Triggering reallocation for short {}", shortRecordId);

        ShortPickRecord record = getShortRecord(shortRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Short record not found: " + shortRecordId));

        allocationFacade.reallocatePickDetail(record.getPickDetailKey());
    }

    @Override
    @Transactional
    public void triggerReplenishment(String shortRecordId, LocationKey location) {
        log.info("Triggering replenishment for short {} at location {}", shortRecordId, location);

        ShortPickRecord record = getShortRecord(shortRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Short record not found: " + shortRecordId));

        replenishmentFacade.triggerUrgentReplenishment(
                location,
                record.getSku(),
                Quantity.of(record.getShortedQty(), "EA"),
                record.getPickDetailKey().toString()
        );
    }

    @Override
    @Transactional
    public void closeWithBackorder(String shortRecordId, String backorderReference) {
        log.info("Closing short {} with backorder {}", shortRecordId, backorderReference);

        ShortPickRecord record = getShortRecord(shortRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Short record not found: " + shortRecordId));

        record.setResolutionStatus(ShortResolutionStatus.RESOLVED);
        record.setResolutionAction(ShortResolutionAction.BACKORDER);
        record.setBackorderReference(backorderReference);
        record.setResolvedTime(LocalDateTime.now());
        shortRepository.save(record);

        allocationFacade.markForBackorder(record.getPickDetailKey(),
                Quantity.of(record.getShortedQty(), "EA"));
    }

    @Override
    @Transactional
    public void cancelShortedLine(String shortRecordId, String reason) {
        log.info("Cancelling shorted line for {}", shortRecordId);

        ShortPickRecord record = getShortRecord(shortRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Short record not found: " + shortRecordId));

        record.setResolutionStatus(ShortResolutionStatus.RESOLVED);
        record.setResolutionAction(ShortResolutionAction.CANCEL);
        record.setResolvedTime(LocalDateTime.now());
        shortRepository.save(record);

        allocationFacade.deallocatePickDetail(record.getPickDetailKey(), reason);
    }

    // Query

    @Override
    public Optional<ShortPickRecord> getShortRecord(String shortRecordId) {
        return shortRepository.findById(shortRecordId);
    }

    @Override
    public List<ShortPickRecord> getShortsByPickDetail(PickDetailKey pickDetailKey) {
        return shortRepository.findByPickDetail(pickDetailKey);
    }

    @Override
    public List<ShortPickRecord> getShortsByOrder(OrderKey orderKey) {
        return shortRepository.findByOrder(orderKey);
    }

    @Override
    public List<ShortPickRecord> getShortsByLocation(LocationKey location) {
        return shortRepository.findByLocation(location);
    }

    @Override
    public List<ShortPickRecord> getShortsByStatus(ShortResolutionStatus status) {
        return shortRepository.findByResolutionStatus(status);
    }

    @Override
    public List<ShortPickRecord> getUnresolvedShorts(String zone, LocalDateTime since) {
        return shortRepository.findUnresolvedByZone(zone);
    }

    // Analytics

    @Override
    public int countShortsToday(String zone) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        return shortRepository.countByZoneAndDateRange(zone, startOfDay, LocalDateTime.now());
    }

    @Override
    public BigDecimal getTotalShortedQuantity(LocalDateTime from, LocalDateTime to) {
        return shortRepository.sumShortedQuantityByDateRange(from, to);
    }

    @Override
    public List<LocationKey> getHighShortLocations(int threshold) {
        return shortRepository.findTopShortLocations(threshold,
                LocalDateTime.now().minusDays(7), LocalDateTime.now());
    }

    @Override
    public List<SkuKey> getHighShortSkus(int threshold) {
        return shortRepository.findTopShortSkus(threshold,
                LocalDateTime.now().minusDays(7), LocalDateTime.now());
    }

    // Notifications

    @Override
    public void notifySupervisor(String shortRecordId, String urgency) {
        log.info("Notifying supervisor for short {} with urgency {}", shortRecordId, urgency);
        // Would integrate with notification service
    }

    @Override
    public void notifyInventoryTeam(String shortRecordId) {
        log.info("Notifying inventory team for short {}", shortRecordId);
        // Would integrate with notification service
    }
}
