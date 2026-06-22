package com.maersk.wms.inbound.service.putaway_service;

import com.maersk.wms.inbound.domain.putaway_service.*;
import com.maersk.wms.inbound.domain.putaway_service.repository.PutawayAlgorithmRepository;
import com.maersk.wms.inbound.service.putaway_service.dto.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service for Putaway Algorithm / Optimization Rules.
 * Part of putaway-service subdomain (putaway/).
 *
 * Responsibilities:
 * - Manage algorithm configurations
 * - Execute location scoring algorithms
 * - Apply optimization rules
 * - Handle multi-criteria decision making
 */
@Service
@Transactional
public class PutawayAlgorithmService {

    private final PutawayAlgorithmRepository algorithmRepository;

    public PutawayAlgorithmService(PutawayAlgorithmRepository algorithmRepository) {
        this.algorithmRepository = algorithmRepository;
    }

    /**
     * Create a new putaway algorithm.
     */
    public PutawayAlgorithm createAlgorithm(CreatePutawayAlgorithmRequest request) {
        validateCreateRequest(request);

        if (algorithmRepository.existsByName(request.getAlgorithmName())) {
            throw new IllegalArgumentException("Algorithm name already exists: " + request.getAlgorithmName());
        }

        PutawayAlgorithm algorithm = PutawayAlgorithm.builder()
            .algorithmName(request.getAlgorithmName())
            .algorithmType(request.getAlgorithmType())
            .active(request.isActive())
            .distanceWeight(request.getDistanceWeight())
            .capacityWeight(request.getCapacityWeight())
            .consolidationWeight(request.getConsolidationWeight())
            .velocityWeight(request.getVelocityWeight())
            .checkCapacity(request.isCheckCapacity())
            .enableConsolidation(request.isEnableConsolidation())
            .useVelocityZoning(request.isUseVelocityZoning())
            .build();

        // Set additional configuration
        algorithm.setDescription(request.getDescription());
        algorithm.setPriority(request.getPriority());
        algorithm.setZonePreferences(request.getZonePreferences());
        algorithm.setPreferredLocationTypes(request.getPreferredLocationTypes());
        algorithm.setMaxSkusPerLocation(request.getMaxSkusPerLocation());
        algorithm.setMaxLotsPerLocation(request.getMaxLotsPerLocation());
        algorithm.setSameSkuOnly(request.isSameSkuOnly());
        algorithm.setSameLotOnly(request.isSameLotOnly());
        algorithm.setMinFillPercent(request.getMinFillPercent());
        algorithm.setMaxFillPercent(request.getMaxFillPercent());
        algorithm.setPreferEmptyLocations(request.isPreferEmptyLocations());
        algorithm.setPreferPartialLocations(request.isPreferPartialLocations());
        algorithm.setOptimizeDistance(request.isOptimizeDistance());
        algorithm.setDistanceMethod(request.getDistanceMethod());
        algorithm.setVelocityAZone(request.getVelocityAZone());
        algorithm.setVelocityBZone(request.getVelocityBZone());
        algorithm.setVelocityCZone(request.getVelocityCZone());
        algorithm.setEnforceFifo(request.isEnforceFifo());
        algorithm.setEnforceFefo(request.isEnforceFefo());
        algorithm.setMinDaysToExpiry(request.getMinDaysToExpiry());
        algorithm.setForReturns(request.isForReturns());
        algorithm.setReturnDefaultZone(request.getReturnDefaultZone());
        algorithm.setDispositionZoneRules(request.getDispositionZoneRules());
        algorithm.setFallbackAlgorithmKey(request.getFallbackAlgorithmKey());
        algorithm.setAllowManualOverride(request.isAllowManualOverride());
        algorithm.setNoLocationFoundAction(request.getNoLocationFoundAction());

        return algorithmRepository.save(algorithm);
    }

    /**
     * Get algorithm by key.
     */
    @Transactional(readOnly = true)
    public Optional<PutawayAlgorithm> getAlgorithm(String algorithmKey) {
        return algorithmRepository.findByKey(algorithmKey);
    }

    /**
     * Get algorithm by name.
     */
    @Transactional(readOnly = true)
    public Optional<PutawayAlgorithm> getByName(String algorithmName) {
        return algorithmRepository.findByName(algorithmName);
    }

    /**
     * Get all algorithms.
     */
    @Transactional(readOnly = true)
    public List<PutawayAlgorithm> getAllAlgorithms() {
        return algorithmRepository.findAll();
    }

    /**
     * Get active algorithms.
     */
    @Transactional(readOnly = true)
    public List<PutawayAlgorithm> getActiveAlgorithms() {
        return algorithmRepository.findActive();
    }

    /**
     * Get algorithms by type.
     */
    @Transactional(readOnly = true)
    public List<PutawayAlgorithm> getByType(AlgorithmType type) {
        return algorithmRepository.findByType(type);
    }

    /**
     * Get default algorithm.
     */
    @Transactional(readOnly = true)
    public PutawayAlgorithm getDefaultAlgorithm() {
        return algorithmRepository.findDefault()
            .orElseThrow(() -> new IllegalStateException("No default algorithm configured"));
    }

    /**
     * Get algorithm for context.
     */
    @Transactional(readOnly = true)
    public PutawayAlgorithm getAlgorithmForContext(PutawayContext context) {
        // Check if context specifies an algorithm
        if (context.getAlgorithmKey() != null) {
            Optional<PutawayAlgorithm> specified = algorithmRepository.findByKey(context.getAlgorithmKey());
            if (specified.isPresent() && specified.get().isActive()) {
                return specified.get();
            }
        }

        // Check for return-specific algorithms
        if (context.isReturn()) {
            List<PutawayAlgorithm> returnAlgorithms = algorithmRepository.findForReturns();
            if (!returnAlgorithms.isEmpty()) {
                return returnAlgorithms.get(0);
            }
        }

        // Return default
        return getDefaultAlgorithm();
    }

    /**
     * Calculate score for location.
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateScore(String algorithmKey, LocationAllocation location, PutawayContext context) {
        PutawayAlgorithm algorithm = algorithmRepository.findByKey(algorithmKey)
            .orElseThrow(() -> new IllegalArgumentException("Algorithm not found: " + algorithmKey));

        return algorithm.calculateScore(location, context);
    }

    /**
     * Check if location meets algorithm constraints.
     */
    @Transactional(readOnly = true)
    public boolean meetsConstraints(String algorithmKey, LocationAllocation location, PutawayContext context) {
        PutawayAlgorithm algorithm = algorithmRepository.findByKey(algorithmKey)
            .orElseThrow(() -> new IllegalArgumentException("Algorithm not found: " + algorithmKey));

        return algorithm.meetsConstraints(location, context);
    }

    /**
     * Update algorithm.
     */
    public PutawayAlgorithm updateAlgorithm(String algorithmKey, UpdatePutawayAlgorithmRequest request) {
        PutawayAlgorithm algorithm = algorithmRepository.findByKey(algorithmKey)
            .orElseThrow(() -> new IllegalArgumentException("Algorithm not found: " + algorithmKey));

        if (request.getDescription() != null) {
            algorithm.setDescription(request.getDescription());
        }
        if (request.getActive() != null) {
            algorithm.setActive(request.getActive());
        }
        if (request.getPriority() != null) {
            algorithm.setPriority(request.getPriority());
        }
        if (request.getDistanceWeight() != null) {
            algorithm.setDistanceWeight(request.getDistanceWeight());
        }
        if (request.getCapacityWeight() != null) {
            algorithm.setCapacityWeight(request.getCapacityWeight());
        }
        if (request.getConsolidationWeight() != null) {
            algorithm.setConsolidationWeight(request.getConsolidationWeight());
        }
        if (request.getVelocityWeight() != null) {
            algorithm.setVelocityWeight(request.getVelocityWeight());
        }

        return algorithmRepository.save(algorithm);
    }

    /**
     * Activate algorithm.
     */
    public PutawayAlgorithm activateAlgorithm(String algorithmKey) {
        PutawayAlgorithm algorithm = algorithmRepository.findByKey(algorithmKey)
            .orElseThrow(() -> new IllegalArgumentException("Algorithm not found: " + algorithmKey));

        algorithm.setActive(true);
        return algorithmRepository.save(algorithm);
    }

    /**
     * Deactivate algorithm.
     */
    public PutawayAlgorithm deactivateAlgorithm(String algorithmKey) {
        PutawayAlgorithm algorithm = algorithmRepository.findByKey(algorithmKey)
            .orElseThrow(() -> new IllegalArgumentException("Algorithm not found: " + algorithmKey));

        algorithm.setActive(false);
        return algorithmRepository.save(algorithm);
    }

    /**
     * Delete algorithm.
     */
    public void deleteAlgorithm(String algorithmKey) {
        if (!algorithmRepository.exists(algorithmKey)) {
            throw new IllegalArgumentException("Algorithm not found: " + algorithmKey);
        }
        algorithmRepository.delete(algorithmKey);
    }

    /**
     * Get zone for disposition using algorithm.
     */
    @Transactional(readOnly = true)
    public String getZoneForDisposition(String algorithmKey, String disposition) {
        PutawayAlgorithm algorithm = algorithmRepository.findByKey(algorithmKey)
            .orElseThrow(() -> new IllegalArgumentException("Algorithm not found: " + algorithmKey));

        return algorithm.getZoneForDisposition(disposition);
    }

    private void validateCreateRequest(CreatePutawayAlgorithmRequest request) {
        if (request.getAlgorithmName() == null || request.getAlgorithmName().isBlank()) {
            throw new IllegalArgumentException("Algorithm name is required");
        }
        if (request.getAlgorithmType() == null) {
            throw new IllegalArgumentException("Algorithm type is required");
        }
    }
}
