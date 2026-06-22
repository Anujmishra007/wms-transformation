package com.maersk.wms.picking.acl.wave;

import com.maersk.wms.picking.shared.kernel.identifiers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Anti-Corruption Layer facade for Wave Service integration.
 * Handles wave status updates and wave information queries.
 */
public interface WaveFacade {

    // Wave Status
    void updateWavePickStatus(WaveKey waveKey, String pickStatus);
    void markWavePickComplete(WaveKey waveKey);
    void markWavePickInProgress(WaveKey waveKey);
    void updateWaveProgress(WaveKey waveKey, int completedTasks, int totalTasks);

    // Query
    Optional<WaveInfo> getWaveInfo(WaveKey waveKey);
    List<OrderKey> getOrdersInWave(WaveKey waveKey);
    int getOpenPickCountForWave(WaveKey waveKey);
    int getTotalPickCountForWave(WaveKey waveKey);

    // Wave Planning
    List<String> getZonesForWave(WaveKey waveKey);
    int getEstimatedPickTime(WaveKey waveKey);
    LocalDateTime getWaveCutoffTime(WaveKey waveKey);

    // Notifications
    void notifyWavePickStarted(WaveKey waveKey);
    void notifyWavePickCompleted(WaveKey waveKey);
    void notifyWaveHasShorts(WaveKey waveKey, int shortCount);

    /**
     * Wave information DTO.
     */
    record WaveInfo(
            WaveKey waveKey,
            String waveType,
            String waveStatus,
            String pickStatus,
            int priority,
            LocalDateTime releaseTime,
            LocalDateTime cutoffTime,
            int totalOrders,
            int totalLines,
            int totalPicks,
            int completedPicks,
            int shortedPicks,
            List<String> zones
    ) {}
}
