package com.maersk.wms.outbound.domain.order_service.service;

import com.maersk.wms.outbound.domain.order_service.model.Wave;
import com.maersk.wms.outbound.domain.order_service.model.WaveStatus;
import com.maersk.wms.outbound.shared.kernel.identifiers.OrderKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.StorerKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.WaveKey;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Wave Management.
 * Part of Order Service bounded context.
 */
public interface WaveManagementService {

    /**
     * Creates a new wave.
     */
    Wave createWave(CreateWaveCommand command);

    /**
     * Gets a wave by key.
     */
    Optional<Wave> getWave(WaveKey waveKey);

    /**
     * Finds waves by storer and status.
     */
    List<Wave> findWaves(StorerKey storerKey, WaveStatus status);

    /**
     * Adds an order to a wave.
     */
    Wave addOrderToWave(WaveKey waveKey, OrderKey orderKey, String userId);

    /**
     * Removes an order from a wave.
     */
    Wave removeOrderFromWave(WaveKey waveKey, OrderKey orderKey, String userId);

    /**
     * Releases a wave for allocation.
     */
    Wave releaseWave(WaveKey waveKey, String userId);

    /**
     * Cancels a wave.
     */
    Wave cancelWave(WaveKey waveKey, String cancelReason, String userId);

    /**
     * Auto-plans orders into waves.
     */
    List<Wave> autoPlanWaves(StorerKey storerKey, WavePlanningCriteria criteria, String userId);

    /**
     * Command for creating a wave.
     */
    record CreateWaveCommand(
            StorerKey storerKey,
            String description,
            List<OrderKey> orderKeys,
            String userId
    ) {}

    /**
     * Criteria for auto-wave planning.
     */
    record WavePlanningCriteria(
            int maxOrdersPerWave,
            int maxLinesPerWave,
            boolean groupByCarrier,
            boolean groupByZone,
            List<String> orderTypes
    ) {}
}
