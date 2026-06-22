package com.maersk.wms.inventory.domain.controls.service.impl;

import com.maersk.wms.inventory.domain.controls.model.CountType;
import com.maersk.wms.inventory.domain.controls.repository.CountTypeRepository;
import com.maersk.wms.inventory.domain.controls.service.CountTypeService;
import com.maersk.wms.inventory.domain.events.CountEvents;
import com.maersk.wms.inventory.shared.kernel.exceptions.*;
import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for inventory count type configuration.
 * Manages counting strategies: Physical, Cycle, Blind, Directed, Spot, Recount.
 */
@Service
@Transactional
public class CountTypeServiceImpl implements CountTypeService {

    private final CountTypeRepository countTypeRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CountTypeServiceImpl(CountTypeRepository countTypeRepository,
                                 ApplicationEventPublisher eventPublisher) {
        this.countTypeRepository = countTypeRepository;
        this.eventPublisher = eventPublisher;
    }

    // ═══════════════════════════════════════════════════════════════
    // COUNT TYPE MANAGEMENT
    // ═══════════════════════════════════════════════════════════════

    @Override
    public CountType createCountType(CountType countType, UserKey createdBy) {
        // Validate unique code
        if (countTypeRepository.existsByCode(countType.countTypeCode(), countType.warehouseKey())) {
            throw new InvalidInventoryOperationException(
                    "Count type code already exists: " + countType.countTypeCode());
        }

        CountType newCountType = CountType.builder()
                .countTypeKey(new CountKey(UUID.randomUUID().toString()))
                .countTypeCode(countType.countTypeCode())
                .description(countType.description())
                .strategy(countType.strategy())
                .warehouseKey(countType.warehouseKey())
                .active(true)
                .blindCount(countType.blindCount())
                .guidedCount(countType.guidedCount())
                .allowAddItems(countType.allowAddItems())
                .requireRecount(countType.requireRecount())
                .recountThresholdPercent(countType.recountThresholdPercent())
                .recountThresholdQty(countType.recountThresholdQty())
                .maxRecounts(countType.maxRecounts())
                .requireApproval(countType.requireApproval())
                .approvalThresholdPercent(countType.approvalThresholdPercent())
                .approvalThresholdQty(countType.approvalThresholdQty())
                .approvalThresholdValue(countType.approvalThresholdValue())
                .locationFilter(countType.locationFilter())
                .storerFilter(countType.storerFilter())
                .skuFilter(countType.skuFilter())
                .priority(countType.priority())
                .isDefault(false)
                .createdBy(createdBy)
                .createdAt(Instant.now())
                .build();

        newCountType = countTypeRepository.save(newCountType);

        // Publish event
        eventPublisher.publishEvent(new CountEvents.CountTypeCreated(
                newCountType.countTypeKey(),
                newCountType.countTypeCode(),
                newCountType.strategy(),
                newCountType.warehouseKey(),
                createdBy,
                Instant.now()
        ));

        return newCountType;
    }

    @Override
    public CountType updateCountType(CountKey countTypeKey, CountType countType, UserKey updatedBy) {
        CountType existing = countTypeRepository.findByKey(countTypeKey)
                .orElseThrow(() -> new InventoryNotFoundException(
                        new InventoryKey(countTypeKey.value())));

        CountType updated = CountType.builder()
                .countTypeKey(existing.countTypeKey())
                .countTypeCode(existing.countTypeCode()) // Code cannot be changed
                .description(countType.description())
                .strategy(countType.strategy())
                .warehouseKey(existing.warehouseKey())
                .active(existing.active())
                .blindCount(countType.blindCount())
                .guidedCount(countType.guidedCount())
                .allowAddItems(countType.allowAddItems())
                .requireRecount(countType.requireRecount())
                .recountThresholdPercent(countType.recountThresholdPercent())
                .recountThresholdQty(countType.recountThresholdQty())
                .maxRecounts(countType.maxRecounts())
                .requireApproval(countType.requireApproval())
                .approvalThresholdPercent(countType.approvalThresholdPercent())
                .approvalThresholdQty(countType.approvalThresholdQty())
                .approvalThresholdValue(countType.approvalThresholdValue())
                .locationFilter(countType.locationFilter())
                .storerFilter(countType.storerFilter())
                .skuFilter(countType.skuFilter())
                .priority(countType.priority())
                .isDefault(existing.isDefault())
                .createdBy(existing.createdBy())
                .createdAt(existing.createdAt())
                .updatedBy(updatedBy)
                .updatedAt(Instant.now())
                .build();

        return countTypeRepository.save(updated);
    }

    @Override
    public void activateCountType(CountKey countTypeKey) {
        CountType countType = countTypeRepository.findByKey(countTypeKey)
                .orElseThrow(() -> new InventoryNotFoundException(
                        new InventoryKey(countTypeKey.value())));

        CountType activated = CountType.builder()
                .countTypeKey(countType.countTypeKey())
                .countTypeCode(countType.countTypeCode())
                .description(countType.description())
                .strategy(countType.strategy())
                .warehouseKey(countType.warehouseKey())
                .active(true)
                .blindCount(countType.blindCount())
                .guidedCount(countType.guidedCount())
                .allowAddItems(countType.allowAddItems())
                .requireRecount(countType.requireRecount())
                .recountThresholdPercent(countType.recountThresholdPercent())
                .recountThresholdQty(countType.recountThresholdQty())
                .maxRecounts(countType.maxRecounts())
                .requireApproval(countType.requireApproval())
                .approvalThresholdPercent(countType.approvalThresholdPercent())
                .approvalThresholdQty(countType.approvalThresholdQty())
                .approvalThresholdValue(countType.approvalThresholdValue())
                .locationFilter(countType.locationFilter())
                .storerFilter(countType.storerFilter())
                .skuFilter(countType.skuFilter())
                .priority(countType.priority())
                .isDefault(countType.isDefault())
                .createdBy(countType.createdBy())
                .createdAt(countType.createdAt())
                .updatedAt(Instant.now())
                .build();

        countTypeRepository.save(activated);
    }

    @Override
    public void deactivateCountType(CountKey countTypeKey) {
        CountType countType = countTypeRepository.findByKey(countTypeKey)
                .orElseThrow(() -> new InventoryNotFoundException(
                        new InventoryKey(countTypeKey.value())));

        if (countType.isDefault()) {
            throw new InvalidInventoryOperationException(
                    "Cannot deactivate default count type: " + countType.countTypeCode());
        }

        CountType deactivated = CountType.builder()
                .countTypeKey(countType.countTypeKey())
                .countTypeCode(countType.countTypeCode())
                .description(countType.description())
                .strategy(countType.strategy())
                .warehouseKey(countType.warehouseKey())
                .active(false)
                .blindCount(countType.blindCount())
                .guidedCount(countType.guidedCount())
                .allowAddItems(countType.allowAddItems())
                .requireRecount(countType.requireRecount())
                .recountThresholdPercent(countType.recountThresholdPercent())
                .recountThresholdQty(countType.recountThresholdQty())
                .maxRecounts(countType.maxRecounts())
                .requireApproval(countType.requireApproval())
                .approvalThresholdPercent(countType.approvalThresholdPercent())
                .approvalThresholdQty(countType.approvalThresholdQty())
                .approvalThresholdValue(countType.approvalThresholdValue())
                .locationFilter(countType.locationFilter())
                .storerFilter(countType.storerFilter())
                .skuFilter(countType.skuFilter())
                .priority(countType.priority())
                .isDefault(false)
                .createdBy(countType.createdBy())
                .createdAt(countType.createdAt())
                .updatedAt(Instant.now())
                .build();

        countTypeRepository.save(deactivated);
    }

    @Override
    public void deleteCountType(CountKey countTypeKey) {
        CountType countType = countTypeRepository.findByKey(countTypeKey)
                .orElseThrow(() -> new InventoryNotFoundException(
                        new InventoryKey(countTypeKey.value())));

        if (countType.isDefault()) {
            throw new InvalidInventoryOperationException(
                    "Cannot delete default count type: " + countType.countTypeCode());
        }

        countTypeRepository.delete(countTypeKey);
    }

    // ═══════════════════════════════════════════════════════════════
    // QUERY OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public Optional<CountType> getCountType(CountKey countTypeKey) {
        return countTypeRepository.findByKey(countTypeKey);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CountType> getCountTypeByCode(String countTypeCode, WarehouseKey warehouseKey) {
        return countTypeRepository.findByCode(countTypeCode, warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CountType> getCountTypes(WarehouseKey warehouseKey) {
        return countTypeRepository.findAll(warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CountType> getActiveCountTypes(WarehouseKey warehouseKey) {
        return countTypeRepository.findActive(warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CountType> getCountTypesByStrategy(CountType.CountStrategy strategy,
                                                    WarehouseKey warehouseKey) {
        return countTypeRepository.findByStrategy(strategy, warehouseKey);
    }

    // ═══════════════════════════════════════════════════════════════
    // VALIDATION OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public boolean requiresRecount(CountKey countTypeKey, double variancePercent, double varianceQty) {
        CountType countType = countTypeRepository.findByKey(countTypeKey)
                .orElseThrow(() -> new InventoryNotFoundException(
                        new InventoryKey(countTypeKey.value())));

        if (!countType.requireRecount()) {
            return false;
        }

        return variancePercent > countType.recountThresholdPercent() ||
                varianceQty > countType.recountThresholdQty();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean requiresApproval(CountKey countTypeKey, double variancePercent,
                                     double varianceQty, double varianceValue) {
        CountType countType = countTypeRepository.findByKey(countTypeKey)
                .orElseThrow(() -> new InventoryNotFoundException(
                        new InventoryKey(countTypeKey.value())));

        if (!countType.requireApproval()) {
            return false;
        }

        return variancePercent > countType.approvalThresholdPercent() ||
                varianceQty > countType.approvalThresholdQty() ||
                varianceValue > countType.approvalThresholdValue();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLocationAllowed(CountKey countTypeKey, String locationCode) {
        CountType countType = countTypeRepository.findByKey(countTypeKey)
                .orElseThrow(() -> new InventoryNotFoundException(
                        new InventoryKey(countTypeKey.value())));

        String filter = countType.locationFilter();
        if (filter == null || filter.isBlank()) {
            return true; // No filter means all locations allowed
        }

        // Simple pattern matching - could be enhanced with regex
        return matchesFilter(locationCode, filter);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isStorerAllowed(CountKey countTypeKey, String storerKey) {
        CountType countType = countTypeRepository.findByKey(countTypeKey)
                .orElseThrow(() -> new InventoryNotFoundException(
                        new InventoryKey(countTypeKey.value())));

        String filter = countType.storerFilter();
        if (filter == null || filter.isBlank()) {
            return true; // No filter means all storers allowed
        }

        return matchesFilter(storerKey, filter);
    }

    // ═══════════════════════════════════════════════════════════════
    // COUNT TYPE DEFAULTS
    // ═══════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public Optional<CountType> getDefaultCycleCountType(WarehouseKey warehouseKey) {
        return countTypeRepository.findDefault(CountType.CountStrategy.CYCLE, warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CountType> getDefaultPhysicalCountType(WarehouseKey warehouseKey) {
        return countTypeRepository.findDefault(CountType.CountStrategy.PHYSICAL, warehouseKey);
    }

    @Override
    public void setAsDefault(CountKey countTypeKey, CountType.CountStrategy strategy) {
        CountType countType = countTypeRepository.findByKey(countTypeKey)
                .orElseThrow(() -> new InventoryNotFoundException(
                        new InventoryKey(countTypeKey.value())));

        if (countType.strategy() != strategy) {
            throw new InvalidInventoryOperationException(
                    "Count type strategy mismatch: expected " + strategy +
                            " but found " + countType.strategy());
        }

        // Remove default from existing default
        countTypeRepository.findDefault(strategy, countType.warehouseKey())
                .ifPresent(existing -> {
                    CountType updated = CountType.builder()
                            .countTypeKey(existing.countTypeKey())
                            .countTypeCode(existing.countTypeCode())
                            .description(existing.description())
                            .strategy(existing.strategy())
                            .warehouseKey(existing.warehouseKey())
                            .active(existing.active())
                            .blindCount(existing.blindCount())
                            .guidedCount(existing.guidedCount())
                            .allowAddItems(existing.allowAddItems())
                            .requireRecount(existing.requireRecount())
                            .recountThresholdPercent(existing.recountThresholdPercent())
                            .recountThresholdQty(existing.recountThresholdQty())
                            .maxRecounts(existing.maxRecounts())
                            .requireApproval(existing.requireApproval())
                            .approvalThresholdPercent(existing.approvalThresholdPercent())
                            .approvalThresholdQty(existing.approvalThresholdQty())
                            .approvalThresholdValue(existing.approvalThresholdValue())
                            .locationFilter(existing.locationFilter())
                            .storerFilter(existing.storerFilter())
                            .skuFilter(existing.skuFilter())
                            .priority(existing.priority())
                            .isDefault(false)
                            .createdBy(existing.createdBy())
                            .createdAt(existing.createdAt())
                            .updatedAt(Instant.now())
                            .build();
                    countTypeRepository.save(updated);
                });

        // Set new default
        CountType newDefault = CountType.builder()
                .countTypeKey(countType.countTypeKey())
                .countTypeCode(countType.countTypeCode())
                .description(countType.description())
                .strategy(countType.strategy())
                .warehouseKey(countType.warehouseKey())
                .active(countType.active())
                .blindCount(countType.blindCount())
                .guidedCount(countType.guidedCount())
                .allowAddItems(countType.allowAddItems())
                .requireRecount(countType.requireRecount())
                .recountThresholdPercent(countType.recountThresholdPercent())
                .recountThresholdQty(countType.recountThresholdQty())
                .maxRecounts(countType.maxRecounts())
                .requireApproval(countType.requireApproval())
                .approvalThresholdPercent(countType.approvalThresholdPercent())
                .approvalThresholdQty(countType.approvalThresholdQty())
                .approvalThresholdValue(countType.approvalThresholdValue())
                .locationFilter(countType.locationFilter())
                .storerFilter(countType.storerFilter())
                .skuFilter(countType.skuFilter())
                .priority(countType.priority())
                .isDefault(true)
                .createdBy(countType.createdBy())
                .createdAt(countType.createdAt())
                .updatedAt(Instant.now())
                .build();

        countTypeRepository.save(newDefault);
    }

    // ═══════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ═══════════════════════════════════════════════════════════════

    private boolean matchesFilter(String value, String filter) {
        if (filter.contains(",")) {
            // List filter: "LOC1,LOC2,LOC3"
            String[] allowedValues = filter.split(",");
            for (String allowed : allowedValues) {
                if (allowed.trim().equalsIgnoreCase(value)) {
                    return true;
                }
            }
            return false;
        } else if (filter.contains("*")) {
            // Wildcard filter: "LOC*" or "*ZONE"
            String regex = filter.replace("*", ".*");
            return value.matches("(?i)" + regex);
        } else {
            // Exact match
            return filter.equalsIgnoreCase(value);
        }
    }
}
