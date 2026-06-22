package com.maersk.wms.outbound.domain.allocation_service.repository;

import com.maersk.wms.outbound.domain.allocation_service.model.PickHeader;
import com.maersk.wms.outbound.domain.allocation_service.model.PickHeaderStatus;
import com.maersk.wms.outbound.shared.kernel.identifiers.OrderKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.PickHeaderKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.StorerKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.WaveKey;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for PickHeader persistence.
 */
public interface PickHeaderRepository {

    PickHeader save(PickHeader pickHeader);

    Optional<PickHeader> findByKey(PickHeaderKey pickHeaderKey);

    List<PickHeader> findByOrder(OrderKey orderKey);

    List<PickHeader> findByWave(WaveKey waveKey);

    List<PickHeader> findByStorerAndStatus(StorerKey storerKey, PickHeaderStatus status);

    List<PickHeader> findReadyForRelease(WaveKey waveKey);

    List<PickHeader> findInProgress(String userId);

    void delete(PickHeaderKey pickHeaderKey);
}
