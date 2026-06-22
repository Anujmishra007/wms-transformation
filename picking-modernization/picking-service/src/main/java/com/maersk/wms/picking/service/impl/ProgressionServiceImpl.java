package com.maersk.wms.picking.service.impl;

import com.maersk.wms.picking.domain.progression_service.model.PickDetailInfo;
import com.maersk.wms.picking.domain.progression_service.model.PickDetailUpdate;
import com.maersk.wms.picking.domain.progression_service.model.ProgressionEventType;
import com.maersk.wms.picking.domain.progression_service.repository.PickDetailInfoRepository;
import com.maersk.wms.picking.domain.progression_service.repository.PickDetailUpdateRepository;
import com.maersk.wms.picking.domain.progression_service.service.ProgressionService;
import com.maersk.wms.picking.shared.kernel.identifiers.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of Progression Service.
 * Tracks and manages pick detail status changes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProgressionServiceImpl implements ProgressionService {

    private final PickDetailUpdateRepository updateRepository;
    private final PickDetailInfoRepository infoRepository;

    // Status Updates

    @Override
    @Transactional
    public PickDetailUpdate recordStatusChange(PickDetailKey pickDetailKey, String oldStatus,
                                               String newStatus, String reason) {
        log.info("Recording status change for {} from {} to {}", pickDetailKey, oldStatus, newStatus);

        PickDetailUpdate update = PickDetailUpdate.builder()
                .updateId(UUID.randomUUID().toString())
                .pickDetailKey(pickDetailKey)
                .eventType(ProgressionEventType.STATUS_CHANGE)
                .oldValue(oldStatus)
                .newValue(newStatus)
                .reason(reason)
                .timestamp(LocalDateTime.now())
                .build();

        return updateRepository.save(update);
    }

    @Override
    @Transactional
    public PickDetailUpdate recordQuantityChange(PickDetailKey pickDetailKey, BigDecimal oldQty,
                                                 BigDecimal newQty, String reason) {
        log.info("Recording quantity change for {} from {} to {}", pickDetailKey, oldQty, newQty);

        PickDetailUpdate update = PickDetailUpdate.builder()
                .updateId(UUID.randomUUID().toString())
                .pickDetailKey(pickDetailKey)
                .eventType(ProgressionEventType.QUANTITY_CHANGE)
                .oldValue(oldQty.toString())
                .newValue(newQty.toString())
                .reason(reason)
                .timestamp(LocalDateTime.now())
                .build();

        return updateRepository.save(update);
    }

    @Override
    @Transactional
    public PickDetailUpdate recordLocationChange(PickDetailKey pickDetailKey, LocationKey oldLocation,
                                                 LocationKey newLocation) {
        log.info("Recording location change for {} from {} to {}", pickDetailKey, oldLocation, newLocation);

        PickDetailUpdate update = PickDetailUpdate.builder()
                .updateId(UUID.randomUUID().toString())
                .pickDetailKey(pickDetailKey)
                .eventType(ProgressionEventType.LOCATION_CHANGE)
                .oldValue(oldLocation != null ? oldLocation.toString() : null)
                .newValue(newLocation != null ? newLocation.toString() : null)
                .timestamp(LocalDateTime.now())
                .build();

        return updateRepository.save(update);
    }

    @Override
    @Transactional
    public PickDetailUpdate recordLpnChange(PickDetailKey pickDetailKey, LpnKey oldLpn, LpnKey newLpn) {
        log.info("Recording LPN change for {} from {} to {}", pickDetailKey, oldLpn, newLpn);

        PickDetailUpdate update = PickDetailUpdate.builder()
                .updateId(UUID.randomUUID().toString())
                .pickDetailKey(pickDetailKey)
                .eventType(ProgressionEventType.LPN_CHANGE)
                .oldValue(oldLpn != null ? oldLpn.toString() : null)
                .newValue(newLpn != null ? newLpn.toString() : null)
                .timestamp(LocalDateTime.now())
                .build();

        return updateRepository.save(update);
    }

    // Pick Confirmation Updates

    @Override
    @Transactional
    public void updatePickedQuantity(PickDetailKey pickDetailKey, BigDecimal pickedQty, UserKey pickedBy) {
        log.info("Updating picked quantity for {} to {} by {}", pickDetailKey, pickedQty, pickedBy);

        PickDetailUpdate update = PickDetailUpdate.builder()
                .updateId(UUID.randomUUID().toString())
                .pickDetailKey(pickDetailKey)
                .eventType(ProgressionEventType.PICK_CONFIRMED)
                .newValue(pickedQty.toString())
                .userId(pickedBy)
                .timestamp(LocalDateTime.now())
                .build();

        updateRepository.save(update);
    }

    @Override
    @Transactional
    public void updatePickedLocation(PickDetailKey pickDetailKey, LocationKey actualLocation) {
        log.info("Updating picked location for {} to {}", pickDetailKey, actualLocation);
        // Implementation would update PICKDETAIL table
    }

    @Override
    @Transactional
    public void updatePickedLpn(PickDetailKey pickDetailKey, LpnKey actualLpn) {
        log.info("Updating picked LPN for {} to {}", pickDetailKey, actualLpn);
        // Implementation would update PICKDETAIL table
    }

    @Override
    @Transactional
    public void updateToLpn(PickDetailKey pickDetailKey, LpnKey toLpn) {
        log.info("Updating to-LPN for {} to {}", pickDetailKey, toLpn);
        // Implementation would update PICKDETAIL table
    }

    @Override
    @Transactional
    public void updateDropLocation(PickDetailKey pickDetailKey, LocationKey dropLocation) {
        log.info("Updating drop location for {} to {}", pickDetailKey, dropLocation);
        // Implementation would update PICKDETAIL table
    }

    // Batch Updates

    @Override
    @Transactional
    public void batchUpdateStatus(List<PickDetailKey> pickDetailKeys, String newStatus, String reason) {
        log.info("Batch updating {} pick details to status {}", pickDetailKeys.size(), newStatus);

        for (PickDetailKey key : pickDetailKeys) {
            recordStatusChange(key, null, newStatus, reason);
        }
    }

    @Override
    @Transactional
    public void batchUpdateAssignment(List<PickDetailKey> pickDetailKeys, UserKey userId, PickListKey listKey) {
        log.info("Batch updating {} pick details - user: {}, list: {}", pickDetailKeys.size(), userId, listKey);

        for (PickDetailKey key : pickDetailKeys) {
            PickDetailUpdate update = PickDetailUpdate.builder()
                    .updateId(UUID.randomUUID().toString())
                    .pickDetailKey(key)
                    .eventType(ProgressionEventType.ASSIGNMENT_CHANGE)
                    .userId(userId)
                    .timestamp(LocalDateTime.now())
                    .build();
            updateRepository.save(update);
        }
    }

    // Audit Trail

    @Override
    public List<PickDetailUpdate> getUpdateHistory(PickDetailKey pickDetailKey) {
        return updateRepository.findByPickDetailOrderByTimestamp(pickDetailKey);
    }

    @Override
    public List<PickDetailUpdate> getUpdatesByUser(UserKey userId, LocalDateTime from, LocalDateTime to) {
        return updateRepository.findByUserAndDateRange(userId, from, to);
    }

    @Override
    public List<PickDetailUpdate> getUpdatesByEventType(ProgressionEventType eventType,
                                                        LocalDateTime from, LocalDateTime to) {
        return updateRepository.findByEventTypeAndDateRange(eventType, from, to);
    }

    // Pick Detail Info

    @Override
    public PickDetailInfo getPickDetailInfo(PickDetailKey pickDetailKey) {
        return infoRepository.findById(pickDetailKey)
                .orElseThrow(() -> new IllegalArgumentException("Pick detail not found: " + pickDetailKey));
    }

    @Override
    public List<PickDetailInfo> getPickDetailsByOrder(OrderKey orderKey) {
        return infoRepository.findByOrder(orderKey);
    }

    @Override
    public List<PickDetailInfo> getPickDetailsByWave(WaveKey waveKey) {
        return infoRepository.findByWave(waveKey);
    }

    @Override
    public List<PickDetailInfo> getPickDetailsByList(PickListKey listKey) {
        return infoRepository.findByPickList(listKey);
    }

    // Completion Tracking

    @Override
    public boolean isOrderFullyPicked(OrderKey orderKey) {
        return !infoRepository.existsOpenByOrder(orderKey);
    }

    @Override
    public boolean isWaveFullyPicked(WaveKey waveKey) {
        return !infoRepository.existsOpenByWave(waveKey);
    }

    @Override
    public BigDecimal getPickedPercentage(OrderKey orderKey) {
        List<PickDetailInfo> details = infoRepository.findByOrder(orderKey);
        if (details.isEmpty()) return BigDecimal.ZERO;

        long completed = details.stream()
                .filter(d -> "9".equals(d.getStatus()))
                .count();

        return BigDecimal.valueOf(completed * 100.0 / details.size());
    }

    @Override
    public int getOpenPickCount(OrderKey orderKey) {
        return infoRepository.countOpenByOrder(orderKey);
    }
}
