package com.maersk.wms.masterdata.domain.repository;

import com.maersk.wms.masterdata.domain.Pack;
import com.maersk.wms.masterdata.domain.PackStatus;
import com.maersk.wms.masterdata.domain.PackType;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Pack entities.
 */
public interface PackRepository {

    Pack save(Pack pack);

    Optional<Pack> findById(Long id);

    Optional<Pack> findByPackCode(String packCode);

    List<Pack> findByStatus(PackStatus status);

    List<Pack> findByPackType(PackType packType);

    List<Pack> findActivePacks();

    List<Pack> findStandardCartons();

    List<Pack> findAll();

    void delete(Pack pack);

    boolean existsByPackCode(String packCode);
}
