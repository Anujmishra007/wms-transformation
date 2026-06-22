package com.maersk.wms.masterdata.domain.product_master.service;

import com.maersk.wms.masterdata.domain.product_master.model.*;
import com.maersk.wms.masterdata.shared.kernel.identifiers.*;
import com.maersk.wms.masterdata.shared.kernel.valueobjects.*;

import java.util.List;
import java.util.Optional;

/**
 * SKU Management Service - manages product master data.
 */
public interface SkuService {

    // SKU CRUD
    SKU createSku(CreateSkuRequest request);
    SKU getSku(SkuKey skuKey);
    SKU getSkuByCode(StorerKey storerKey, String skuCode);
    List<SKU> getSkusByStorer(StorerKey storerKey);
    List<SKU> getActiveSkusByStorer(StorerKey storerKey);
    List<SKU> searchSkus(SkuSearchCriteria criteria);

    void updateSku(SkuKey skuKey, UpdateSkuRequest request);
    void activateSku(SkuKey skuKey);
    void deactivateSku(SkuKey skuKey);
    void discontinueSku(SkuKey skuKey);
    void deleteSku(SkuKey skuKey);

    // Dimensions
    void addDimension(SkuKey skuKey, SKUDimension dimension);
    void updateDimension(DimensionKey dimensionKey, SKUDimension dimension);
    void removeDimension(DimensionKey dimensionKey);
    List<SKUDimension> getSkuDimensions(SkuKey skuKey);
    Optional<SKUDimension> getDimensionByPackType(SkuKey skuKey, String packType);

    // Lottables
    void addLottable(SkuKey skuKey, SKULottable lottable);
    void updateLottable(LottableKey lottableKey, SKULottable lottable);
    void removeLottable(LottableKey lottableKey);
    List<SKULottable> getSkuLottables(SkuKey skuKey);

    // Bulk Operations
    void importSkus(List<CreateSkuRequest> requests);
    List<SKU> exportSkus(StorerKey storerKey);

    // Validation
    List<String> validateSku(SKU sku);
    boolean skuExists(StorerKey storerKey, String skuCode);

    // Request Records
    record CreateSkuRequest(
            StorerKey storerKey,
            String skuCode,
            String description,
            String skuGroup,
            String productClass,
            String baseUom,
            Dimensions dimensions,
            LottableConfig lottableConfig,
            String storageProfile,
            String putawayStrategy,
            String rotationRule
    ) {}

    record UpdateSkuRequest(
            String description,
            String skuGroup,
            String productClass,
            Dimensions dimensions,
            LottableConfig lottableConfig,
            String storageProfile,
            String putawayStrategy,
            String rotationRule
    ) {}

    record SkuSearchCriteria(
            StorerKey storerKey,
            String skuCode,
            String description,
            String skuGroup,
            String productClass,
            SKU.SkuStatus status,
            int limit,
            int offset
    ) {}
}
