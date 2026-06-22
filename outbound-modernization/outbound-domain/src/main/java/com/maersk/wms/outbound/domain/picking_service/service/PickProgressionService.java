package com.maersk.wms.outbound.domain.picking_service.service;

import com.maersk.wms.outbound.domain.picking_service.model.PickDetailInfo;
import com.maersk.wms.outbound.domain.picking_service.model.PickDetailUpdate;
import com.maersk.wms.outbound.shared.kernel.identifiers.PickDetailKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.PickHeaderKey;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for Pick Detail Progression.
 * Manages the pick status lifecycle.
 * Part of Picking Operations Service bounded context.
 */
public interface PickProgressionService {

    /**
     * Starts a pick.
     */
    PickDetailInfo startPick(PickDetailKey pickDetailKey, StartPickCommand command);

    /**
     * Confirms a pick quantity.
     */
    PickDetailInfo confirmPick(PickDetailKey pickDetailKey, ConfirmPickCommand command);

    /**
     * Completes a pick.
     */
    PickDetailInfo completePick(PickDetailKey pickDetailKey, CompletePickCommand command);

    /**
     * Gets the current state of a pick detail.
     */
    PickDetailInfo getPickDetail(PickDetailKey pickDetailKey);

    /**
     * Gets all pick details for a pick header.
     */
    List<PickDetailInfo> getPickDetailsForHeader(PickHeaderKey pickHeaderKey);

    /**
     * Gets the update history for a pick detail.
     */
    List<PickDetailUpdate> getPickHistory(PickDetailKey pickDetailKey);

    /**
     * Command to start a pick.
     */
    record StartPickCommand(
            String userId,
            String deviceId,
            String scannedLocation,
            String scannedLpn
    ) {}

    /**
     * Command to confirm a pick.
     */
    record ConfirmPickCommand(
            String userId,
            String deviceId,
            BigDecimal qtyPicked,
            String scannedSku,
            String toLpn
    ) {}

    /**
     * Command to complete a pick.
     */
    record CompletePickCommand(
            String userId,
            String deviceId,
            BigDecimal finalQtyPicked,
            String dropLocation
    ) {}
}
