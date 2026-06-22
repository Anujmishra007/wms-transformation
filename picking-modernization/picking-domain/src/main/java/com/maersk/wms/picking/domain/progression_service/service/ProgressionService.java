package com.maersk.wms.picking.domain.progression_service.service;

import com.maersk.wms.picking.domain.progression_service.model.PickDetailInfo;
import com.maersk.wms.picking.domain.progression_service.model.PickDetailUpdate;
import com.maersk.wms.picking.domain.progression_service.model.ProgressionEventType;
import com.maersk.wms.picking.shared.kernel.identifiers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Pick Detail Progression Service - tracks and manages pick detail status changes.
 * Handles the PICKDETAIL table updates and status progression.
 */
public interface ProgressionService {

    // Status Updates
    PickDetailUpdate recordStatusChange(PickDetailKey pickDetailKey, String oldStatus, String newStatus, String reason);
    PickDetailUpdate recordQuantityChange(PickDetailKey pickDetailKey, BigDecimal oldQty, BigDecimal newQty, String reason);
    PickDetailUpdate recordLocationChange(PickDetailKey pickDetailKey, LocationKey oldLocation, LocationKey newLocation);
    PickDetailUpdate recordLpnChange(PickDetailKey pickDetailKey, LpnKey oldLpn, LpnKey newLpn);

    // Pick Confirmation Updates
    void updatePickedQuantity(PickDetailKey pickDetailKey, BigDecimal pickedQty, UserKey pickedBy);
    void updatePickedLocation(PickDetailKey pickDetailKey, LocationKey actualLocation);
    void updatePickedLpn(PickDetailKey pickDetailKey, LpnKey actualLpn);
    void updateToLpn(PickDetailKey pickDetailKey, LpnKey toLpn);
    void updateDropLocation(PickDetailKey pickDetailKey, LocationKey dropLocation);

    // Batch Updates
    void batchUpdateStatus(List<PickDetailKey> pickDetailKeys, String newStatus, String reason);
    void batchUpdateAssignment(List<PickDetailKey> pickDetailKeys, UserKey userId, PickListKey listKey);

    // Audit Trail
    List<PickDetailUpdate> getUpdateHistory(PickDetailKey pickDetailKey);
    List<PickDetailUpdate> getUpdatesByUser(UserKey userId, LocalDateTime from, LocalDateTime to);
    List<PickDetailUpdate> getUpdatesByEventType(ProgressionEventType eventType, LocalDateTime from, LocalDateTime to);

    // Pick Detail Info
    PickDetailInfo getPickDetailInfo(PickDetailKey pickDetailKey);
    List<PickDetailInfo> getPickDetailsByOrder(OrderKey orderKey);
    List<PickDetailInfo> getPickDetailsByWave(WaveKey waveKey);
    List<PickDetailInfo> getPickDetailsByList(PickListKey listKey);

    // Completion Tracking
    boolean isOrderFullyPicked(OrderKey orderKey);
    boolean isWaveFullyPicked(WaveKey waveKey);
    BigDecimal getPickedPercentage(OrderKey orderKey);
    int getOpenPickCount(OrderKey orderKey);
}
