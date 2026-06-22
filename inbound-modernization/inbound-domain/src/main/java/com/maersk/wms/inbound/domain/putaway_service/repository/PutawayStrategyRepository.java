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

    Optional<PutawayStrategy> findByName(String strategyName);

    List<PutawayStrategy> findAll();

    List<PutawayStrategy> findByType(PutawayStrategyType type);

    List<PutawayStrategy> findActiveStrategies();

    List<PutawayStrategy> findActive();

    Optional<PutawayStrategy> findDefaultStrategy();

    Optional<PutawayStrategy> findDefault();

    List<PutawayStrategy> findForReturns();

    List<PutawayStrategy> findByPriority();

    boolean existsByName(String strategyName);

    boolean exists(String strategyKey);

    PutawayStrategy save(PutawayStrategy strategy);

    void delete(String strategyKey);
}
