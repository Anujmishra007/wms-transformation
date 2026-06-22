package com.maersk.wms.outbound.domain.allocation_service.service;

import com.maersk.wms.outbound.domain.allocation_service.model.PickHeader;
import com.maersk.wms.outbound.shared.kernel.identifiers.OrderKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.PickHeaderKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.WaveKey;

import java.util.List;

/**
 * Service interface for Release (releasing allocated inventory for picking).
 * Part of Inventory Allocation Service - Release module.
 */
public interface ReleaseService {

    /**
     * Releases a single pick header for picking.
     */
    ReleaseResult releasePickHeader(PickHeaderKey pickHeaderKey, ReleaseContext context);

    /**
     * Releases all pick headers for an order.
     */
    ReleaseResult releaseOrder(OrderKey orderKey, ReleaseContext context);

    /**
     * Releases all pick headers for a wave.
     */
    ReleaseResult releaseWave(WaveKey waveKey, ReleaseContext context);

    /**
     * Releases pick headers based on criteria.
     */
    ReleaseResult releaseByCriteria(ReleaseCriteria criteria, ReleaseContext context);

    /**
     * Gets pick headers ready for release.
     */
    List<PickHeader> getPickHeadersReadyForRelease(WaveKey waveKey);

    /**
     * Context for release.
     */
    record ReleaseContext(
            String userId,
            String equipmentType,
            String zone,
            boolean createPickLists,
            boolean optimizeRoute
    ) {}

    /**
     * Criteria for selective release.
     */
    record ReleaseCriteria(
            WaveKey waveKey,
            List<String> zones,
            List<String> carrierCodes,
            int maxPickHeaders,
            int maxLines,
            boolean priorityOnly
    ) {}

    /**
     * Result of release.
     */
    record ReleaseResult(
            boolean success,
            int pickHeadersReleased,
            int pickDetailsReleased,
            List<String> pickListIds,
            List<String> messages
    ) {}
}
