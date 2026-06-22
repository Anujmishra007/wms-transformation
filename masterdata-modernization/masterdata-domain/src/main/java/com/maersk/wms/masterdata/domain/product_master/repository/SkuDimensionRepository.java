package com.maersk.wms.masterdata.domain.product_master.repository;

import com.maersk.wms.masterdata.domain.product_master.model.SKUDimension;
import com.maersk.wms.masterdata.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for SKU Dimension entity.
 */
public interface SkuDimensionRepository {

    SKUDimension save(SKUDimension dimension);

    Optional<SKUDimension> findByDimensionKey(DimensionKey dimensionKey);

    List<SKUDimension> findBySku(SkuKey skuKey);

    Optional<SKUDimension> findBySkuAndPackType(SkuKey skuKey, String packType);

    List<SKUDimension> findActiveBySkuKey(SkuKey skuKey);

    void delete(SKUDimension dimension);

    void deleteBySkuKey(SkuKey skuKey);
}
