package com.maersk.wms.outbound.domain.order_service.repository;

import com.maersk.wms.outbound.domain.order_service.model.Wave;
import com.maersk.wms.outbound.domain.order_service.model.WaveStatus;
import com.maersk.wms.outbound.shared.kernel.identifiers.StorerKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.WaveKey;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Wave persistence.
 */
public interface WaveRepository {

    Wave save(Wave wave);

    Optional<Wave> findByKey(WaveKey waveKey);

    List<Wave> findByStorerAndStatus(StorerKey storerKey, WaveStatus status);

    List<Wave> findReadyForRelease(StorerKey storerKey);

    List<Wave> findInProgress(StorerKey storerKey);

    void delete(WaveKey waveKey);
}
