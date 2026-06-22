package com.maersk.wms.outbound.domain.allocation_service.repository;

import com.maersk.wms.outbound.domain.allocation_service.model.PickDetail;
import com.maersk.wms.outbound.domain.allocation_service.model.PickDetailStatus;
import com.maersk.wms.outbound.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for PickDetail persistence.
 */
public interface PickDetailRepository {

    PickDetail save(PickDetail pickDetail);

    List<PickDetail> saveAll(List<PickDetail> pickDetails);

    Optional<PickDetail> findByKey(PickDetailKey pickDetailKey);

    List<PickDetail> findByPickHeader(PickHeaderKey pickHeaderKey);

    List<PickDetail> findByOrder(OrderKey orderKey);

    List<PickDetail> findByOrderLine(OrderKey orderKey, int lineNumber);

    List<PickDetail> findByStatus(PickDetailStatus status);

    List<PickDetail> findByLocation(LocationKey locationKey);

    List<PickDetail> findByUserAndStatus(String userId, PickDetailStatus status);

    List<PickDetail> findOpenByZone(String zone);

    void delete(PickDetailKey pickDetailKey);
}
