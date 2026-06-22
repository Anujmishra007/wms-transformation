package com.maersk.wms.masterdata.domain.product_master.repository;

import com.maersk.wms.masterdata.domain.product_master.model.SKULottable;
import com.maersk.wms.masterdata.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for SKU Lottable entity.
 */
public interface SkuLottableRepository {

    SKULottable save(SKULottable lottable);

    Optional<SKULottable> findByLottableKey(LottableKey lottableKey);

    List<SKULottable> findBySku(SkuKey skuKey);

    List<SKULottable> findActiveBySku(SkuKey skuKey);

    Optional<SKULottable> findBySkuAndField(SkuKey skuKey, String lottableField);

    List<SKULottable> findBySkuAndUseInAllocation(SkuKey skuKey);

    void delete(SKULottable lottable);

    void deleteBySkuKey(SkuKey skuKey);
}
