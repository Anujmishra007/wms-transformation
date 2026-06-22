package com.maersk.wms.outbound.domain.picking_service.repository;

import com.maersk.wms.outbound.domain.picking_service.model.ShortPickRecord;
import com.maersk.wms.outbound.domain.picking_service.model.ShortResolutionStatus;
import com.maersk.wms.outbound.shared.kernel.identifiers.PickDetailKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.StorerKey;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ShortPickRecord persistence.
 */
public interface ShortPickRepository {

    ShortPickRecord save(ShortPickRecord shortPick);

    Optional<ShortPickRecord> findById(String shortPickId);

    List<ShortPickRecord> findByPickDetail(PickDetailKey pickDetailKey);

    List<ShortPickRecord> findByStorerAndStatus(StorerKey storerKey, ShortResolutionStatus status);

    List<ShortPickRecord> findByStorerAndDateRange(StorerKey storerKey, LocalDate fromDate, LocalDate toDate);

    List<ShortPickRecord> findPending(StorerKey storerKey);
}
