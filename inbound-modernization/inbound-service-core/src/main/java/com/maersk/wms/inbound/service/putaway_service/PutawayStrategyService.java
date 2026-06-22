package com.maersk.wms.inbound.service.putaway_service;

import com.maersk.wms.inbound.domain.putaway_service.*;
import com.maersk.wms.inbound.domain.putaway_service.repository.PutawayStrategyRepository;
import com.maersk.wms.inbound.service.putaway_service.dto.*;
import com.maersk.wms.inbound.shared.kernel.identifiers.SkuKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for Putaway Strategy selection.
 * Part of putaway-service subdomain (putaway/).
 *
 * Responsibilities:
 * - Manage putaway strategy configurations
 * - Select appropriate strategy based on context
 * - Determine strategy rules and zone mappings
 * - Handle strategy priority and fallback
 */
@Service
@Transactional
public class PutawayStrategyService {

    private final PutawayStrategyRepository strategyRepository;

    public PutawayStrategyService(PutawayStrategyRepository strategyRepository) {
        this.strategyRepository = strategyRepository;
    }

    /**
     * Create a new putaway strategy.
     */
    public PutawayStrategy createStrategy(CreatePutawayStrategyRequest request) {
        validateCreateRequest(request);

        // Check for duplicate name
        if (strategyRepository.existsByName(request.getStrategyName())) {
            throw new IllegalArgumentException("Strategy name already exists: " + request.getStrategyName());
        }

        PutawayStrategy strategy = PutawayStrategy.builder()
            .strategyName(request.getStrategyName())
            .description(request.getDescription())
            .type(request.getStrategyType())
            .active(request.isActive())
            .priority(request.getPriority())
            .allowedZones(request.getAllowedZones())
            .excludedZones(request.getExcludedZones())
            .defaultZone(request.getDefaultZone())
            .preferredLocationTypes(request.getPreferredLocationTypes())
            .allowMixedSku(request.isAllowMixedSku())
            .allowMixedLot(request.isAllowMixedLot())
            .checkCapacity(request.isCheckCapacity())
            .consolidate(request.isConsolidate())
            .enforceFifo(request.isEnforceFifo())
            .checkExpiry(request.isCheckExpiry())
            .minDaysToExpiry(request.getMinDaysToExpiry())
            .useVelocity(request.isUseVelocity())
            .velocityZoneMapping(request.getVelocityZoneMapping())
            .forReturns(request.isForReturns())
            .returnZone(request.getReturnZone())
            .dispositionZoneMapping(request.getDispositionZoneMapping())
            .build();

        return strategyRepository.save(strategy);
    }

    /**
     * Get strategy by key.
     */
    @Transactional(readOnly = true)
    public Optional<PutawayStrategy> getStrategy(String strategyKey) {
        return strategyRepository.findByKey(strategyKey);
    }

    /**
     * Get strategy by name.
     */
    @Transactional(readOnly = true)
    public Optional<PutawayStrategy> getByName(String strategyName) {
        return strategyRepository.findByName(strategyName);
    }

    /**
     * Get all strategies.
     */
    @Transactional(readOnly = true)
    public List<PutawayStrategy> getAllStrategies() {
        return strategyRepository.findAll();
    }

    /**
     * Get active strategies.
     */
    @Transactional(readOnly = true)
    public List<PutawayStrategy> getActiveStrategies() {
        return strategyRepository.findActive();
    }

    /**
     * Get strategies by type.
     */
    @Transactional(readOnly = true)
    public List<PutawayStrategy> getByType(PutawayStrategyType type) {
        return strategyRepository.findByType(type);
    }

    /**
     * Get strategies ordered by priority.
     */
    @Transactional(readOnly = true)
    public List<PutawayStrategy> getByPriority() {
        return strategyRepository.findByPriority();
    }

    /**
     * Get strategies for returns.
     */
    @Transactional(readOnly = true)
    public List<PutawayStrategy> getReturnStrategies() {
        return strategyRepository.findForReturns();
    }

    /**
     * Select best strategy for context.
     */
    @Transactional(readOnly = true)
    public PutawayStrategy selectStrategy(PutawayContext context) {
        List<PutawayStrategy> strategies = strategyRepository.findByPriority();

        for (PutawayStrategy strategy : strategies) {
            if (!strategy.isActive()) {
                continue;
            }

            // Check if strategy applies to returns
            if (context.isReturn() && !strategy.isForReturns()) {
                continue;
            }

            // Check if strategy applies to disposition
            if (context.isReturn() && context.getDisposition() != null) {
                if (!strategy.appliesTo(true, context.getDisposition())) {
                    continue;
                }
            }

            // Check zone restrictions
            if (!isZoneAllowed(strategy, context.getPreferredZone())) {
                continue;
            }

            return strategy;
        }

        // Return default strategy
        return strategyRepository.findDefault()
            .orElseThrow(() -> new IllegalStateException("No default putaway strategy configured"));
    }

    /**
     * Select strategy for SKU.
     */
    @Transactional(readOnly = true)
    public PutawayStrategy selectStrategyForSku(StorerKey storerKey, SkuKey skuKey) {
        // Build context for SKU
        PutawayContext context = PutawayContext.builder()
            .storerKey(storerKey)
            .skuKey(skuKey)
            .build();

        return selectStrategy(context);
    }

    /**
     * Get zone for disposition (returns).
     */
    @Transactional(readOnly = true)
    public String getZoneForDisposition(String strategyKey, String disposition) {
        PutawayStrategy strategy = strategyRepository.findByKey(strategyKey)
            .orElseThrow(() -> new IllegalArgumentException("Strategy not found: " + strategyKey));

        return strategy.getZoneForDisposition(disposition);
    }

    /**
     * Update strategy.
     */
    public PutawayStrategy updateStrategy(String strategyKey, UpdatePutawayStrategyRequest request) {
        PutawayStrategy strategy = strategyRepository.findByKey(strategyKey)
            .orElseThrow(() -> new IllegalArgumentException("Strategy not found: " + strategyKey));

        if (request.getDescription() != null) {
            strategy.setDescription(request.getDescription());
        }
        if (request.getActive() != null) {
            strategy.setActive(request.getActive());
        }
        if (request.getPriority() != null) {
            strategy.setPriority(request.getPriority());
        }
        if (request.getAllowedZones() != null) {
            strategy.setAllowedZones(request.getAllowedZones());
        }
        if (request.getExcludedZones() != null) {
            strategy.setExcludedZones(request.getExcludedZones());
        }
        if (request.getDefaultZone() != null) {
            strategy.setDefaultZone(request.getDefaultZone());
        }
        if (request.getCheckCapacity() != null) {
            strategy.setCheckCapacity(request.getCheckCapacity());
        }
        if (request.getConsolidate() != null) {
            strategy.setConsolidate(request.getConsolidate());
        }

        return strategyRepository.save(strategy);
    }

    /**
     * Activate strategy.
     */
    public PutawayStrategy activateStrategy(String strategyKey) {
        PutawayStrategy strategy = strategyRepository.findByKey(strategyKey)
            .orElseThrow(() -> new IllegalArgumentException("Strategy not found: " + strategyKey));

        strategy.setActive(true);
        return strategyRepository.save(strategy);
    }

    /**
     * Deactivate strategy.
     */
    public PutawayStrategy deactivateStrategy(String strategyKey) {
        PutawayStrategy strategy = strategyRepository.findByKey(strategyKey)
            .orElseThrow(() -> new IllegalArgumentException("Strategy not found: " + strategyKey));

        strategy.setActive(false);
        return strategyRepository.save(strategy);
    }

    /**
     * Delete strategy.
     */
    public void deleteStrategy(String strategyKey) {
        if (!strategyRepository.exists(strategyKey)) {
            throw new IllegalArgumentException("Strategy not found: " + strategyKey);
        }
        strategyRepository.delete(strategyKey);
    }

    private void validateCreateRequest(CreatePutawayStrategyRequest request) {
        if (request.getStrategyName() == null || request.getStrategyName().isBlank()) {
            throw new IllegalArgumentException("Strategy name is required");
        }
        if (request.getStrategyType() == null) {
            throw new IllegalArgumentException("Strategy type is required");
        }
    }

    private boolean isZoneAllowed(PutawayStrategy strategy, String zone) {
        if (zone == null) {
            return true;
        }
        if (strategy.getExcludedZones() != null && strategy.getExcludedZones().contains(zone)) {
            return false;
        }
        if (strategy.getAllowedZones() != null && !strategy.getAllowedZones().isEmpty()) {
            return strategy.getAllowedZones().contains(zone);
        }
        return true;
    }
}
