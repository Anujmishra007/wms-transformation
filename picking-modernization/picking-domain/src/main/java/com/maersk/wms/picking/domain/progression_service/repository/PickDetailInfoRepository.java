package com.maersk.wms.picking.domain.progression_service.repository;

import com.maersk.wms.picking.domain.progression_service.model.PickDetailInfo;
import com.maersk.wms.picking.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PickDetailInfo read model.
 */
public interface PickDetailInfoRepository {

    // Read operations (this is primarily a read model)
    Optional<PickDetailInfo> findById(PickDetailKey pickDetailKey);
    List<PickDetailInfo> findByOrder(OrderKey orderKey);
    List<PickDetailInfo> findByWave(WaveKey waveKey);
    List<PickDetailInfo> findByPickList(PickListKey listKey);
    List<PickDetailInfo> findByLocation(LocationKey location);
    List<PickDetailInfo> findBySku(SkuKey sku);
    List<PickDetailInfo> findByUser(UserKey userId);
    List<PickDetailInfo> findByStatus(String status);

    // Aggregations
    int countByOrderAndStatus(OrderKey orderKey, String status);
    int countByWaveAndStatus(WaveKey waveKey, String status);
    int countOpenByOrder(OrderKey orderKey);

    // Exists
    boolean existsOpenByOrder(OrderKey orderKey);
    boolean existsOpenByWave(WaveKey waveKey);
}
