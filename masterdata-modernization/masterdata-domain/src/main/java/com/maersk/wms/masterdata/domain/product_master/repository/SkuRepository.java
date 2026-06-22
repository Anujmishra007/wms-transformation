package com.maersk.wms.masterdata.domain.product_master.repository;

import com.maersk.wms.masterdata.domain.product_master.model.SKU;
import com.maersk.wms.masterdata.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for SKU aggregate.
 */
public interface SkuRepository {

    SKU save(SKU sku);

    Optional<SKU> findBySkuKey(SkuKey skuKey);

    Optional<SKU> findByStorerAndCode(StorerKey storerKey, String skuCode);

    List<SKU> findByStorer(StorerKey storerKey);

    List<SKU> findByStorerAndStatus(StorerKey storerKey, SKU.SkuStatus status);

    List<SKU> findBySkuGroup(StorerKey storerKey, String skuGroup);

    List<SKU> findByProductClass(StorerKey storerKey, String productClass);

    List<SKU> search(StorerKey storerKey, String searchTerm, int limit, int offset);

    boolean existsByStorerAndCode(StorerKey storerKey, String skuCode);

    void delete(SKU sku);

    int countByStorer(StorerKey storerKey);

    int countByStorerAndStatus(StorerKey storerKey, SKU.SkuStatus status);
}
