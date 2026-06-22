package com.maersk.wms.outbound.domain.picking_service.repository;

import com.maersk.wms.outbound.domain.picking_service.model.PickList;
import com.maersk.wms.outbound.domain.picking_service.model.PickListStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for PickList persistence.
 */
public interface PickListRepository {

    PickList save(PickList pickList);

    Optional<PickList> findById(String pickListId);

    List<PickList> findByUser(String userId);

    List<PickList> findByStatus(PickListStatus status);

    List<PickList> findAvailableByZone(String zone);

    List<PickList> findByUserAndStatus(String userId, PickListStatus status);
}
