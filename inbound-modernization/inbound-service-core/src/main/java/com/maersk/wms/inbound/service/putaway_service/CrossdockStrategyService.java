package com.maersk.wms.inbound.service.putaway_service;

import com.maersk.wms.inbound.domain.putaway_service.*;
import com.maersk.wms.inbound.domain.putaway_service.repository.CrossdockStrategyRepository;
import com.maersk.wms.inbound.service.putaway_service.dto.*;
import com.maersk.wms.inbound.shared.kernel.identifiers.SkuKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Service for Crossdock Strategy / Determination Logic.
 * Part of putaway-service subdomain (putaway/).
 *
 * Responsibilities:
 * - Manage crossdock strategy configurations
 * - Determine crossdock eligibility
 * - Match inbound to outbound demand
 * - Select optimal crossdock approach
 */
@Service
@Transactional
public class CrossdockStrategyService {

    private final CrossdockStrategyRepository strategyRepository;

    public CrossdockStrategyService(CrossdockStrategyRepository strategyRepository) {
        this.strategyRepository = strategyRepository;
    }

    /**
     * Create a new crossdock strategy.
     */
    public CrossdockStrategy createStrategy(CreateCrossdockStrategyRequest request) {
        validateCreateRequest(request);

        if (strategyRepository.existsByName(request.getStrategyName())) {
            throw new IllegalArgumentException("Strategy name already exists: " + request.getStrategyName());
        }

        CrossdockStrategy strategy = CrossdockStrategy.builder()
            .strategyName(request.getStrategyName())
            .strategyType(request.getStrategyType())
            .active(request.isActive())
            .matchBySku(request.isMatchBySku())
            .matchByLot(request.isMatchByLot())
            .matchByStorer(request.isMatchByStorer())
            .matchByOrder(request.isMatchByOrder())
            .allowPartialMatch(request.isAllowPartialMatch())
            .demandHorizonDays(request.getDemandHorizonDays())
            .build();

        // Set additional configuration
        strategy.setDescription(request.getDescription());
        strategy.setPriority(request.getPriority());
        strategy.setMatchByWave(request.isMatchByWave());
        strategy.setMatchByShipment(request.isMatchByShipment());
        strategy.setDemandHorizonHours(request.getDemandHorizonHours());
        strategy.setCheckFutureDemand(request.isCheckFutureDemand());
        strategy.setCheckCurrentDemand(request.isCheckCurrentDemand());
        strategy.setMinMatchPercent(request.getMinMatchPercent());
        strategy.setMinQuantity(request.getMinQuantity());
        strategy.setMaxQuantity(request.getMaxQuantity());
        strategy.setPrioritizeOldestDemand(request.isPrioritizeOldestDemand());
        strategy.setPrioritizeHighestPriority(request.isPrioritizeHighestPriority());
        strategy.setPrioritizeNearestShipDate(request.isPrioritizeNearestShipDate());
        strategy.setCrossdockZones(request.getCrossdockZones());
        strategy.setStagingZones(request.getStagingZones());
        strategy.setDefaultStagingZone(request.getDefaultStagingZone());
        strategy.setAllowedSkuTypes(request.getAllowedSkuTypes());
        strategy.setExcludedSkuTypes(request.getExcludedSkuTypes());
        strategy.setExcludeHazmat(request.isExcludeHazmat());
        strategy.setExcludeTemperatureControlled(request.isExcludeTemperatureControlled());
        strategy.setAllowedOrderTypes(request.getAllowedOrderTypes());
        strategy.setAllowedCarriers(request.getAllowedCarriers());
        strategy.setCheckTimeWindow(request.isCheckTimeWindow());
        strategy.setMinLeadTimeHours(request.getMinLeadTimeHours());
        strategy.setMaxLeadTimeHours(request.getMaxLeadTimeHours());
        strategy.setFallbackToStorage(request.isFallbackToStorage());
        strategy.setNoMatchAction(request.getNoMatchAction());

        return strategyRepository.save(strategy);
    }

    /**
     * Get strategy by key.
     */
    @Transactional(readOnly = true)
    public Optional<CrossdockStrategy> getStrategy(String strategyKey) {
        return strategyRepository.findByKey(strategyKey);
    }

    /**
     * Get strategy by name.
     */
    @Transactional(readOnly = true)
    public Optional<CrossdockStrategy> getByName(String strategyName) {
        return strategyRepository.findByName(strategyName);
    }

    /**
     * Get all strategies.
     */
    @Transactional(readOnly = true)
    public List<CrossdockStrategy> getAllStrategies() {
        return strategyRepository.findAll();
    }

    /**
     * Get active strategies.
     */
    @Transactional(readOnly = true)
    public List<CrossdockStrategy> getActiveStrategies() {
        return strategyRepository.findActive();
    }

    /**
     * Get strategies by type.
     */
    @Transactional(readOnly = true)
    public List<CrossdockStrategy> getByType(CrossdockStrategyType type) {
        return strategyRepository.findByType(type);
    }

    /**
     * Get opportunistic strategies.
     */
    @Transactional(readOnly = true)
    public List<CrossdockStrategy> getOpportunisticStrategies() {
        return strategyRepository.findOpportunistic();
    }

    /**
     * Get planned strategies.
     */
    @Transactional(readOnly = true)
    public List<CrossdockStrategy> getPlannedStrategies() {
        return strategyRepository.findPlanned();
    }

    /**
     * Evaluate crossdock eligibility for candidate.
     */
    @Transactional(readOnly = true)
    public CrossdockEvaluation evaluateCrossdock(CrossdockCandidate candidate, List<CrossdockDemand> availableDemand) {
        CrossdockEvaluation evaluation = new CrossdockEvaluation();
        evaluation.setCandidate(candidate);
        evaluation.setEligible(false);
        evaluation.setMatches(new ArrayList<>());

        List<CrossdockStrategy> strategies = strategyRepository.findByPriority();

        for (CrossdockStrategy strategy : strategies) {
            if (!strategy.isActive()) {
                continue;
            }

            // Check if candidate is eligible for this strategy
            if (!strategy.isEligible(candidate)) {
                continue;
            }

            // Find matching demands
            List<CrossdockDemand> matches = availableDemand.stream()
                .filter(demand -> strategy.matchesDemand(candidate, demand))
                .sorted(Comparator.comparingInt(d -> -strategy.calculatePriority(d)))
                .toList();

            if (!matches.isEmpty()) {
                evaluation.setEligible(true);
                evaluation.setSelectedStrategy(strategy);
                evaluation.setMatches(matches);
                evaluation.setStagingZone(strategy.getStagingZone(candidate));

                // Calculate total matchable quantity
                BigDecimal totalMatchable = matches.stream()
                    .map(CrossdockDemand::getOpenQuantity)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                evaluation.setTotalMatchableQuantity(totalMatchable);

                break;
            }
        }

        return evaluation;
    }

    /**
     * Check if SKU is crossdock eligible.
     */
    @Transactional(readOnly = true)
    public boolean isSkuCrossdockEligible(StorerKey storerKey, SkuKey skuKey, String skuType) {
        List<CrossdockStrategy> strategies = strategyRepository.findActive();

        for (CrossdockStrategy strategy : strategies) {
            // Check SKU type restrictions
            if (!strategy.getAllowedSkuTypes().isEmpty()
                && !strategy.getAllowedSkuTypes().contains(skuType)) {
                continue;
            }
            if (strategy.getExcludedSkuTypes().contains(skuType)) {
                continue;
            }

            // Check storer restrictions
            if (!strategy.getAllowedStorers().isEmpty()
                && !strategy.getAllowedStorers().contains(storerKey)) {
                continue;
            }

            return true;
        }

        return false;
    }

    /**
     * Get staging zone for candidate.
     */
    @Transactional(readOnly = true)
    public String getStagingZone(String strategyKey, CrossdockCandidate candidate) {
        CrossdockStrategy strategy = strategyRepository.findByKey(strategyKey)
            .orElseThrow(() -> new IllegalArgumentException("Strategy not found: " + strategyKey));

        return strategy.getStagingZone(candidate);
    }

    /**
     * Update strategy.
     */
    public CrossdockStrategy updateStrategy(String strategyKey, UpdateCrossdockStrategyRequest request) {
        CrossdockStrategy strategy = strategyRepository.findByKey(strategyKey)
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
        if (request.getDemandHorizonDays() != null) {
            strategy.setDemandHorizonDays(request.getDemandHorizonDays());
        }
        if (request.getMinMatchPercent() != null) {
            strategy.setMinMatchPercent(request.getMinMatchPercent());
        }
        if (request.getCrossdockZones() != null) {
            strategy.setCrossdockZones(request.getCrossdockZones());
        }
        if (request.getStagingZones() != null) {
            strategy.setStagingZones(request.getStagingZones());
        }
        if (request.getDefaultStagingZone() != null) {
            strategy.setDefaultStagingZone(request.getDefaultStagingZone());
        }

        return strategyRepository.save(strategy);
    }

    /**
     * Activate strategy.
     */
    public CrossdockStrategy activateStrategy(String strategyKey) {
        CrossdockStrategy strategy = strategyRepository.findByKey(strategyKey)
            .orElseThrow(() -> new IllegalArgumentException("Strategy not found: " + strategyKey));

        strategy.setActive(true);
        return strategyRepository.save(strategy);
    }

    /**
     * Deactivate strategy.
     */
    public CrossdockStrategy deactivateStrategy(String strategyKey) {
        CrossdockStrategy strategy = strategyRepository.findByKey(strategyKey)
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

    private void validateCreateRequest(CreateCrossdockStrategyRequest request) {
        if (request.getStrategyName() == null || request.getStrategyName().isBlank()) {
            throw new IllegalArgumentException("Strategy name is required");
        }
        if (request.getStrategyType() == null) {
            throw new IllegalArgumentException("Strategy type is required");
        }
    }
}
