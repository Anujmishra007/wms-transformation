package com.maersk.wms.inbound.domain.putaway_service.repository;

import com.maersk.wms.inbound.domain.putaway_service.PutawayStrategy;
import com.maersk.wms.inbound.domain.putaway_service.PutawayStrategyType;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PutawayStrategy.
 */
public interface PutawayStrategyRepository {

    Optional<PutawayStrategy> findByKey(String strategyKey);

    List<PutawayStrategy> findByType(PutawayStrategyType type);

    List<PutawayStrategy> findActiveStrategies();

    Optional<PutawayStrategy> findDefaultStrategy();

    Optional<PutawayStrategy> findForReturns();

    PutawayStrategy save(PutawayStrategy strategy);

    void delete(String strategyKey);
}
