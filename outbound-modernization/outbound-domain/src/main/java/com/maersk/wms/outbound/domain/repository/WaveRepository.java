package com.maersk.wms.outbound.domain.repository;

import com.maersk.wms.outbound.domain.Wave;
import com.maersk.wms.outbound.domain.WaveStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Wave entity.
 */
public interface WaveRepository {

    Optional<Wave> findByKey(String waveKey);

    Optional<Wave> findByWaveKey(String waveKey);

    List<Wave> findByStatus(WaveStatus status);

    List<Wave> findByStorerKey(String storerKey);

    List<Wave> findByDateRange(LocalDateTime fromDate, LocalDateTime toDate);

    List<Wave> findActiveWaves(String storerKey);

    Wave save(Wave wave);

    void delete(String waveKey);

    String generateWaveKey();
}
