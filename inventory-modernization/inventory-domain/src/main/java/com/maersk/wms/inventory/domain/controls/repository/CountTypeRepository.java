package com.maersk.wms.inventory.domain.controls.repository;

import com.maersk.wms.inventory.domain.controls.model.CountType;
import com.maersk.wms.inventory.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CountType configuration entities.
 * Manages counting strategies: Physical, Cycle, Blind, Directed, Spot, Recount.
 */
public interface CountTypeRepository {

    // ═══════════════════════════════════════════════════════════════
    // CRUD OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Save count type.
     */
    CountType save(CountType countType);

    /**
     * Save multiple count types.
     */
    List<CountType> saveAll(List<CountType> countTypes);

    /**
     * Delete count type.
     */
    void delete(CountKey countTypeKey);

    /**
     * Delete count type by code.
     */
    void deleteByCode(String countTypeCode, WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // FIND OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Find count type by key.
     */
    Optional<CountType> findByKey(CountKey countTypeKey);

    /**
     * Find count type by code.
     */
    Optional<CountType> findByCode(String countTypeCode, WarehouseKey warehouseKey);

    /**
     * Check if count type exists.
     */
    boolean existsByKey(CountKey countTypeKey);

    /**
     * Check if count type code exists.
     */
    boolean existsByCode(String countTypeCode, WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // LIST OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Find all count types for warehouse.
     */
    List<CountType> findAll(WarehouseKey warehouseKey);

    /**
     * Find active count types.
     */
    List<CountType> findActive(WarehouseKey warehouseKey);

    /**
     * Find inactive count types.
     */
    List<CountType> findInactive(WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // STRATEGY QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Find count types by strategy.
     */
    List<CountType> findByStrategy(CountType.CountStrategy strategy, WarehouseKey warehouseKey);

    /**
     * Find active count types by strategy.
     */
    List<CountType> findActiveByStrategy(CountType.CountStrategy strategy, WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // DEFAULT QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Find default count type for strategy.
     */
    Optional<CountType> findDefault(CountType.CountStrategy strategy, WarehouseKey warehouseKey);

    /**
     * Find default cycle count type.
     */
    Optional<CountType> findDefaultCycleCount(WarehouseKey warehouseKey);

    /**
     * Find default physical count type.
     */
    Optional<CountType> findDefaultPhysicalCount(WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // VARIANCE THRESHOLD QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Find count types requiring recount.
     */
    List<CountType> findRequiringRecount(WarehouseKey warehouseKey);

    /**
     * Find count types requiring approval.
     */
    List<CountType> findRequiringApproval(WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // COUNT OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Count all count types for warehouse.
     */
    long count(WarehouseKey warehouseKey);

    /**
     * Count active count types.
     */
    long countActive(WarehouseKey warehouseKey);

    /**
     * Count by strategy.
     */
    long countByStrategy(CountType.CountStrategy strategy, WarehouseKey warehouseKey);
}
