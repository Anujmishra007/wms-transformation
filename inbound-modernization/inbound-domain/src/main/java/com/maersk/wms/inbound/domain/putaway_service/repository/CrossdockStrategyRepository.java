package com.maersk.wms.inbound.domain.putaway_service.repository;

import com.maersk.wms.inbound.domain.putaway_service.CrossdockStrategy;
import com.maersk.wms.inbound.domain.putaway_service.CrossdockStrategyType;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;

import java.util.List;
import java.util.Optional;

/**
 * Repository for CrossdockStrategy in putaway-service subdomain.
 */
public interface CrossdockStrategyRepository {

    Optional<CrossdockStrategy> findByKey(String strategyKey);

    Optional<CrossdockStrategy> findByName(String strategyName);

    List<CrossdockStrategy> findAll();

    List<CrossdockStrategy> findActive();

    List<CrossdockStrategy> findByType(CrossdockStrategyType type);

    List<CrossdockStrategy> findByPriority();

    List<CrossdockStrategy> findForStorer(StorerKey storerKey);

    List<CrossdockStrategy> findOpportunistic();

    List<CrossdockStrategy> findPlanned();

    Optional<CrossdockStrategy> findDefault();

    CrossdockStrategy save(CrossdockStrategy strategy);

    void delete(String strategyKey);

    boolean exists(String strategyKey);

    boolean existsByName(String strategyName);
}
