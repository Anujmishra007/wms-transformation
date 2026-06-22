package com.maersk.wms.masterdata.domain.operational_masters.repository;

import com.maersk.wms.masterdata.domain.operational_masters.model.Storer;
import com.maersk.wms.masterdata.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Storer aggregate.
 */
public interface StorerRepository {

    Storer save(Storer storer);

    Optional<Storer> findByStorerKey(StorerKey storerKey);

    Optional<Storer> findByStorerCode(String storerCode);

    List<Storer> findAll();

    List<Storer> findByStatus(Storer.StorerStatus status);

    List<Storer> findByType(Storer.StorerType storerType);

    List<Storer> findByCustomerGroup(String customerGroup);

    List<Storer> search(String searchTerm, int limit, int offset);

    boolean existsByStorerCode(String storerCode);

    void delete(Storer storer);

    int countByStatus(Storer.StorerStatus status);
}
