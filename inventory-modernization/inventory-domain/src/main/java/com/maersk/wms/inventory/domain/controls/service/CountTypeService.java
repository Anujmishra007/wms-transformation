package com.maersk.wms.inventory.domain.controls.service;

import com.maersk.wms.inventory.domain.controls.model.CountType;
import com.maersk.wms.inventory.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for inventory count type configuration.
 * Manages counting strategies: Physical, Cycle, Blind, Directed, Spot, Recount.
 */
public interface CountTypeService {

    // ═══════════════════════════════════════════════════════════════
    // COUNT TYPE MANAGEMENT
    // ═══════════════════════════════════════════════════════════════

    /**
     * Create a new count type.
     */
    CountType createCountType(CountType countType, UserKey createdBy);

    /**
     * Update count type configuration.
     */
    CountType updateCountType(CountKey countTypeKey, CountType countType, UserKey updatedBy);

    /**
     * Activate count type.
     */
    void activateCountType(CountKey countTypeKey);

    /**
     * Deactivate count type.
     */
    void deactivateCountType(CountKey countTypeKey);

    /**
     * Delete count type.
     */
    void deleteCountType(CountKey countTypeKey);

    // ═══════════════════════════════════════════════════════════════
    // QUERY OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Get count type by key.
     */
    Optional<CountType> getCountType(CountKey countTypeKey);

    /**
     * Get count type by code.
     */
    Optional<CountType> getCountTypeByCode(String countTypeCode, WarehouseKey warehouseKey);

    /**
     * Get all count types for warehouse.
     */
    List<CountType> getCountTypes(WarehouseKey warehouseKey);

    /**
     * Get active count types.
     */
    List<CountType> getActiveCountTypes(WarehouseKey warehouseKey);

    /**
     * Get count types by strategy.
     */
    List<CountType> getCountTypesByStrategy(CountType.CountStrategy strategy, WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // VALIDATION OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Check if recount is required based on variance.
     */
    boolean requiresRecount(CountKey countTypeKey, double variancePercent, double varianceQty);

    /**
     * Check if approval is required based on variance.
     */
    boolean requiresApproval(CountKey countTypeKey, double variancePercent,
                              double varianceQty, double varianceValue);

    /**
     * Check if location is allowed for count type.
     */
    boolean isLocationAllowed(CountKey countTypeKey, String locationCode);

    /**
     * Check if storer is allowed for count type.
     */
    boolean isStorerAllowed(CountKey countTypeKey, String storerKey);

    // ═══════════════════════════════════════════════════════════════
    // COUNT TYPE DEFAULTS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Get default cycle count type.
     */
    Optional<CountType> getDefaultCycleCountType(WarehouseKey warehouseKey);

    /**
     * Get default physical count type.
     */
    Optional<CountType> getDefaultPhysicalCountType(WarehouseKey warehouseKey);

    /**
     * Set as default count type.
     */
    void setAsDefault(CountKey countTypeKey, CountType.CountStrategy strategy);
}
