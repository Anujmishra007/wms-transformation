package com.maersk.wms.inbound.service.putaway_service;

import com.maersk.wms.inbound.domain.putaway_service.*;
import com.maersk.wms.inbound.domain.putaway_service.repository.LocationAllocationRepository;
import com.maersk.wms.inbound.service.putaway_service.dto.*;
import com.maersk.wms.inbound.shared.kernel.identifiers.LocationKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.SkuKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;
import com.maersk.wms.inbound.shared.kernel.valueobjects.Quantity;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for Storage Location Allocation.
 * Part of putaway-service subdomain (putaway/).
 *
 * Responsibilities:
 * - Find available locations for putaway
 * - Reserve and allocate locations
 * - Track location capacity and utilization
 * - Manage allocation lifecycle
 * - Handle allocation expiration
 *
 * Legacy SP References:
 * - nsp_DirectedPutaway → suggestLocations()
 * - nsp_GetPutawayStrategy → via PutawayStrategyService
 * - nsp_FindAvailableLocation → findAvailableLocations()
 * - nsp_AllocateLocation → reserveLocation()
 */
@Service
@Transactional
public class LocationAllocationService {

    private final LocationAllocationRepository allocationRepository;
    private final PutawayAlgorithmService algorithmService;

    public LocationAllocationService(LocationAllocationRepository allocationRepository,
                                     PutawayAlgorithmService algorithmService) {
        this.allocationRepository = allocationRepository;
        this.algorithmService = algorithmService;
    }

    /**
     * Find and suggest locations for putaway.
     */
    @Transactional(readOnly = true)
    public List<LocationSuggestion> suggestLocations(LocationSuggestionRequest request) {
        PutawayContext context = buildContext(request);

        // Get available locations in allowed zones
        List<LocationAllocation> availableLocations = findAvailableLocations(
            request.getZone(),
            request.getLocationType(),
            new Quantity(request.getRequiredCapacity(), request.getUom())
        );

        // Score and rank locations using algorithm
        PutawayAlgorithm algorithm = algorithmService.getAlgorithmForContext(context);

        return availableLocations.stream()
            .filter(loc -> algorithm.meetsConstraints(loc, context))
            .map(loc -> {
                BigDecimal score = algorithm.calculateScore(loc, context);
                return toSuggestion(loc, score);
            })
            .sorted(Comparator.comparing(LocationSuggestion::getScore).reversed())
            .limit(request.getMaxSuggestions() > 0 ? request.getMaxSuggestions() : 5)
            .collect(Collectors.toList());
    }

    /**
     * Suggest single best location.
     */
    @Transactional(readOnly = true)
    public Optional<LocationSuggestion> suggestLocation(LocationSuggestionRequest request) {
        List<LocationSuggestion> suggestions = suggestLocations(request);
        return suggestions.isEmpty() ? Optional.empty() : Optional.of(suggestions.get(0));
    }

    /**
     * Reserve a location for putaway.
     */
    public LocationAllocation reserveLocation(ReserveLocationRequest request) {
        LocationAllocation allocation = allocationRepository.findByLocationKey(
            new LocationKey(request.getLocationKey()))
            .orElseThrow(() -> new IllegalArgumentException("Location not found: " + request.getLocationKey()));

        if (!allocation.isAvailable()) {
            throw new IllegalStateException("Location not available for reservation");
        }

        Quantity quantity = new Quantity(request.getQuantity(), request.getUom());
        if (!allocation.hasCapacityFor(quantity)) {
            throw new IllegalStateException("Location does not have sufficient capacity");
        }

        allocation.reserve(quantity);
        allocation.setPutawayKey(request.getPutawayKey());
        allocation.setSkuKey(new SkuKey(request.getStorerKey(), request.getSku()));
        allocation.setStorerKey(new StorerKey(request.getStorerKey()));
        allocation.setAllocatedBy(request.getAllocatedBy());

        // Set expiration if configured
        if (request.getExpirationMinutes() > 0) {
            allocation.setExpiresAt(Instant.now().plus(request.getExpirationMinutes(), ChronoUnit.MINUTES));
        }

        return allocationRepository.save(allocation);
    }

    /**
     * Confirm allocation after putaway complete.
     */
    public LocationAllocation confirmAllocation(String allocationKey, ConfirmAllocationRequest request) {
        LocationAllocation allocation = allocationRepository.findByKey(allocationKey)
            .orElseThrow(() -> new IllegalArgumentException("Allocation not found: " + allocationKey));

        Quantity actualQty = new Quantity(request.getActualQuantity(), request.getUom());
        allocation.confirm(actualQty, request.getConfirmedBy());

        return allocationRepository.save(allocation);
    }

    /**
     * Release allocation (cancel reservation).
     */
    public LocationAllocation releaseAllocation(String allocationKey, String reason) {
        LocationAllocation allocation = allocationRepository.findByKey(allocationKey)
            .orElseThrow(() -> new IllegalArgumentException("Allocation not found: " + allocationKey));

        allocation.release(reason);
        return allocationRepository.save(allocation);
    }

    /**
     * Get allocation by key.
     */
    @Transactional(readOnly = true)
    public Optional<LocationAllocation> getAllocation(String allocationKey) {
        return allocationRepository.findByKey(allocationKey);
    }

    /**
     * Get allocation by location.
     */
    @Transactional(readOnly = true)
    public Optional<LocationAllocation> getByLocation(LocationKey locationKey) {
        return allocationRepository.findByLocationKey(locationKey);
    }

    /**
     * Get allocations by putaway key.
     */
    @Transactional(readOnly = true)
    public List<LocationAllocation> getByPutawayKey(String putawayKey) {
        return allocationRepository.findByPutawayKey(putawayKey);
    }

    /**
     * Get available locations in zone.
     */
    @Transactional(readOnly = true)
    public List<LocationAllocation> getAvailableInZone(String zone) {
        return allocationRepository.findAvailableInZone(zone);
    }

    /**
     * Get available locations for SKU (consolidation).
     */
    @Transactional(readOnly = true)
    public List<LocationAllocation> getAvailableForSku(SkuKey skuKey, String zone) {
        return allocationRepository.findAvailableForSku(skuKey, zone);
    }

    /**
     * Get empty locations in zone.
     */
    @Transactional(readOnly = true)
    public List<LocationAllocation> getEmptyLocations(String zone) {
        return allocationRepository.findEmptyLocations(zone);
    }

    /**
     * Get partial locations in zone (for consolidation).
     */
    @Transactional(readOnly = true)
    public List<LocationAllocation> getPartialLocations(String zone) {
        return allocationRepository.findPartialLocations(zone);
    }

    /**
     * Validate if location is valid for putaway.
     */
    @Transactional(readOnly = true)
    public boolean validateLocation(String locationKey, LocationSuggestionRequest request) {
        Optional<LocationAllocation> allocation = allocationRepository.findByLocationKey(
            new LocationKey(locationKey));

        if (allocation.isEmpty()) {
            return false;
        }

        LocationAllocation loc = allocation.get();
        if (!loc.isAvailable()) {
            return false;
        }

        // Check capacity
        Quantity required = new Quantity(request.getRequiredCapacity(), request.getUom());
        if (!loc.hasCapacityFor(required)) {
            return false;
        }

        // Build context and check algorithm constraints
        PutawayContext context = buildContext(request);
        PutawayAlgorithm algorithm = algorithmService.getAlgorithmForContext(context);

        return algorithm.meetsConstraints(loc, context);
    }

    /**
     * Expire old reservations.
     */
    public int expireOldReservations() {
        List<LocationAllocation> expired = allocationRepository.findExpired();
        int count = 0;

        for (LocationAllocation allocation : expired) {
            if (allocation.isExpired()) {
                allocation.expire();
                allocationRepository.save(allocation);
                count++;
            }
        }

        return count;
    }

    /**
     * Get zone capacity statistics.
     */
    @Transactional(readOnly = true)
    public ZoneCapacityStats getZoneCapacity(String zone) {
        List<LocationAllocation> locations = allocationRepository.findByZone(zone);

        ZoneCapacityStats stats = new ZoneCapacityStats();
        stats.setZone(zone);
        stats.setTotalLocations(locations.size());
        stats.setAvailableLocations(allocationRepository.countAvailableInZone(zone));
        stats.setReservedLocations(allocationRepository.countByZoneAndStatus(zone, AllocationStatus.RESERVED));
        stats.setFullLocations(allocationRepository.countByZoneAndStatus(zone, AllocationStatus.CONFIRMED));

        BigDecimal totalCapacity = locations.stream()
            .map(LocationAllocation::getTotalCapacity)
            .filter(c -> c != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal usedCapacity = locations.stream()
            .map(LocationAllocation::getUsedCapacity)
            .filter(c -> c != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.setTotalCapacity(totalCapacity);
        stats.setUsedCapacity(usedCapacity);
        stats.setAvailableCapacity(totalCapacity.subtract(usedCapacity));
        stats.setUtilizationPercent(totalCapacity.compareTo(BigDecimal.ZERO) > 0
            ? usedCapacity.divide(totalCapacity, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
            : BigDecimal.ZERO);

        return stats;
    }

    private List<LocationAllocation> findAvailableLocations(String zone, String locationType, Quantity requiredCapacity) {
        List<LocationAllocation> locations;

        if (zone != null && locationType != null) {
            locations = allocationRepository.findByZone(zone).stream()
                .filter(l -> locationType.equals(l.getLocationType()))
                .filter(LocationAllocation::isAvailable)
                .filter(l -> l.hasCapacityFor(requiredCapacity))
                .collect(Collectors.toList());
        } else if (zone != null) {
            locations = allocationRepository.findAvailableInZone(zone).stream()
                .filter(l -> l.hasCapacityFor(requiredCapacity))
                .collect(Collectors.toList());
        } else {
            locations = allocationRepository.findWithCapacity(requiredCapacity.getValue()).stream()
                .filter(LocationAllocation::isAvailable)
                .collect(Collectors.toList());
        }

        return locations;
    }

    private PutawayContext buildContext(LocationSuggestionRequest request) {
        return PutawayContext.builder()
            .storerKey(new StorerKey(request.getStorerKey()))
            .skuKey(request.getSku())
            .quantity(new Quantity(request.getRequiredCapacity(), request.getUom()))
            .velocityClass(request.getVelocityClass())
            .isReturn(request.isReturn())
            .disposition(request.getDisposition())
            .preferConsolidation(request.isPreferConsolidation())
            .preferEmpty(request.isPreferEmpty())
            .build();
    }

    private LocationSuggestion toSuggestion(LocationAllocation allocation, BigDecimal score) {
        LocationSuggestion suggestion = new LocationSuggestion();
        suggestion.setLocationKey(allocation.getLocationKey().getValue());
        suggestion.setZone(allocation.getZone());
        suggestion.setLocationType(allocation.getLocationType());
        suggestion.setAisle(allocation.getAisle());
        suggestion.setBay(allocation.getBay());
        suggestion.setLevel(allocation.getLevel());
        suggestion.setPosition(allocation.getPosition());
        suggestion.setAvailableCapacity(allocation.getAvailableCapacity());
        suggestion.setScore(score);
        suggestion.setScoreReason(allocation.getScoreReason());
        suggestion.setHasSameSku(allocation.getCurrentSkuCount() > 0);
        suggestion.setEmpty(allocation.getCurrentSkuCount() == 0);
        return suggestion;
    }
}
