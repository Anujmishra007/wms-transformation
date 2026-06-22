package com.maersk.wms.inbound.domain.putaway_service.repository;

import com.maersk.wms.inbound.domain.putaway_service.AlgorithmType;
import com.maersk.wms.inbound.domain.putaway_service.PutawayAlgorithm;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PutawayAlgorithm in putaway-service subdomain.
 */
public interface PutawayAlgorithmRepository {

    Optional<PutawayAlgorithm> findByKey(String algorithmKey);

    Optional<PutawayAlgorithm> findByName(String algorithmName);

    List<PutawayAlgorithm> findAll();

    List<PutawayAlgorithm> findActive();

    List<PutawayAlgorithm> findByType(AlgorithmType type);

    List<PutawayAlgorithm> findByPriority();

    List<PutawayAlgorithm> findForReturns();

    Optional<PutawayAlgorithm> findDefault();

    PutawayAlgorithm save(PutawayAlgorithm algorithm);

    void delete(String algorithmKey);

    boolean exists(String algorithmKey);

    boolean existsByName(String algorithmName);
}
