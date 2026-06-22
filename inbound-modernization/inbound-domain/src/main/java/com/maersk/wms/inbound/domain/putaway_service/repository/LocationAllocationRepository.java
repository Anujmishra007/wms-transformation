package com.maersk.wms.inbound.domain.putaway_service.repository;

import com.maersk.wms.inbound.domain.putaway_service.AllocationStatus;
import com.maersk.wms.inbound.domain.putaway_service.AllocationType;
import com.maersk.wms.inbound.domain.putaway_service.LocationAllocation;
import com.maersk.wms.inbound.shared.kernel.identifiers.LocationKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.SkuKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository for LocationAllocation in putaway-service subdomain.
 */
public interface LocationAllocationRepository {

    Optional<LocationAllocation> findByKey(String allocationKey);

    Optional<LocationAllocation> findByLocationKey(LocationKey locationKey);

    List<LocationAllocation> findByStorerKey(StorerKey storerKey);

    List<LocationAllocation> findBySkuKey(SkuKey skuKey);

    List<LocationAllocation> findByStatus(AllocationStatus status);

    List<LocationAllocation> findByType(AllocationType type);

    List<LocationAllocation> findByZone(String zone);

    List<LocationAllocation> findByLocationType(String locationType);

    List<LocationAllocation> findAvailableInZone(String zone);

    List<LocationAllocation> findAvailableForSku(SkuKey skuKey, String zone);

    List<LocationAllocation> findWithCapacity(BigDecimal requiredCapacity);

    List<LocationAllocation> findEmptyLocations(String zone);

    List<LocationAllocation> findPartialLocations(String zone);

    List<LocationAllocation> findByPutawayKey(String putawayKey);

    List<LocationAllocation> findExpired();

    List<LocationAllocation> findReservedByUser(String userId);

    LocationAllocation save(LocationAllocation allocation);

    void delete(String allocationKey);

    boolean exists(String allocationKey);

    long countByZoneAndStatus(String zone, AllocationStatus status);

    long countAvailableInZone(String zone);
}
