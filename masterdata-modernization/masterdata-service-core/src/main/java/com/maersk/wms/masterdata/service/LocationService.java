package com.maersk.wms.masterdata.service;

import com.maersk.wms.masterdata.domain.Location;
import com.maersk.wms.masterdata.domain.LocationStatus;
import com.maersk.wms.masterdata.domain.LocationType;
import com.maersk.wms.masterdata.domain.repository.LocationRepository;
import com.maersk.wms.masterdata.plugin.LocationPlugin;
import com.maersk.wms.masterdata.plugin.MasterDataPluginContext;
import com.maersk.wms.masterdata.plugin.MasterDataPluginRegistry;
import com.maersk.wms.masterdata.plugin.PluginResult;
import com.maersk.wms.masterdata.rules.LocationValidationFacts;
import com.maersk.wms.masterdata.rules.LocationValidationResult;
import com.maersk.wms.masterdata.rules.MasterDataRulesEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for location master data operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {

    private final LocationRepository locationRepository;
    private final MasterDataPluginRegistry pluginRegistry;
    private final MasterDataRulesEngine rulesEngine;

    /**
     * Create a new location.
     */
    @Transactional
    public Location createLocation(Location location, MasterDataPluginContext context) {
        log.info("Creating location: {} for client: {}", location.getLocationCode(), context.getClientCode());

        // Check if location already exists
        if (locationRepository.existsByLocationCode(location.getLocationCode())) {
            throw new MasterDataOperationException("Location already exists: " + location.getLocationCode());
        }

        // Execute before create plugins
        PluginResult beforeResult = pluginRegistry.executeAll(
                LocationPlugin.class,
                context,
                plugin -> plugin.beforeLocationCreate(location, context)
        );

        if (!beforeResult.isSuccess()) {
            throw new MasterDataOperationException("Location creation blocked: " + beforeResult.getErrorMessage());
        }

        // Validate with rules engine
        LocationValidationFacts facts = buildValidationFacts(location, context, "CREATE");
        LocationValidationResult validationResult = rulesEngine.evaluateLocationRules(facts);

        if (!validationResult.isValid()) {
            throw new MasterDataOperationException(
                    "Location validation failed: " + String.join(", ", validationResult.getErrors()));
        }

        // Calculate pick path sequence through plugin
        Optional<LocationPlugin> locationPlugin = pluginRegistry.getPlugin(LocationPlugin.class, context);
        int pickPathSeq = locationPlugin
                .map(p -> p.calculatePickPathSequence(location, context))
                .orElse(location.getPickPathSequence());
        location.setPickPathSequence(pickPathSeq);

        // Transform through plugin
        Location transformedLocation = locationPlugin.map(p -> p.transformLocation(location, context)).orElse(location);

        // Set defaults
        transformedLocation.setStatus(LocationStatus.AVAILABLE);
        transformedLocation.setCreatedBy(context.getUserId());
        transformedLocation.setCreatedAt(LocalDateTime.now());

        // Save
        Location savedLocation = locationRepository.save(transformedLocation);

        // Execute after create plugins
        pluginRegistry.executeAll(
                LocationPlugin.class,
                context,
                plugin -> plugin.afterLocationCreate(savedLocation, context)
        );

        log.info("Location created: {}", savedLocation.getLocationCode());
        return savedLocation;
    }

    /**
     * Update an existing location.
     */
    @Transactional
    public Location updateLocation(String locationCode, Location updates, MasterDataPluginContext context) {
        log.info("Updating location: {}", locationCode);

        Location existingLocation = locationRepository.findByLocationCode(locationCode)
                .orElseThrow(() -> new MasterDataOperationException("Location not found: " + locationCode));

        // Execute before update plugins
        PluginResult beforeResult = pluginRegistry.executeAll(
                LocationPlugin.class,
                context,
                plugin -> plugin.beforeLocationUpdate(updates, context)
        );

        if (!beforeResult.isSuccess()) {
            throw new MasterDataOperationException("Location update blocked: " + beforeResult.getErrorMessage());
        }

        // Merge updates
        mergeUpdates(existingLocation, updates);

        // Set audit fields
        existingLocation.setUpdatedBy(context.getUserId());
        existingLocation.setUpdatedAt(LocalDateTime.now());

        // Save
        Location savedLocation = locationRepository.save(existingLocation);

        // Execute after update plugins
        pluginRegistry.executeAll(
                LocationPlugin.class,
                context,
                plugin -> plugin.afterLocationUpdate(savedLocation, context)
        );

        log.info("Location updated: {}", savedLocation.getLocationCode());
        return savedLocation;
    }

    /**
     * Get location by code.
     */
    public Optional<Location> getLocation(String locationCode) {
        return locationRepository.findByLocationCode(locationCode);
    }

    /**
     * Get locations by zone.
     */
    public List<Location> getLocationsByZone(String zone) {
        return locationRepository.findByZone(zone);
    }

    /**
     * Get pick locations by zone.
     */
    public List<Location> getPickLocations(String zone) {
        return locationRepository.findPickLocations(zone);
    }

    /**
     * Get locations by type.
     */
    public List<Location> getLocationsByType(LocationType type) {
        return locationRepository.findByLocationType(type);
    }

    /**
     * Generate location code using plugin.
     */
    public String generateLocationCode(String zone, String aisle, String bay,
                                        String level, String position,
                                        MasterDataPluginContext context) {
        Optional<LocationPlugin> plugin = pluginRegistry.getPlugin(LocationPlugin.class, context);
        return plugin.map(p -> p.generateLocationCode(zone, aisle, bay, level, position, context))
                .orElse(zone + "-" + aisle + "-" + bay + "-" + level + "-" + position);
    }

    private void mergeUpdates(Location existing, Location updates) {
        if (updates.getDescription() != null) existing.setDescription(updates.getDescription());
        if (updates.getStatus() != null) existing.setStatus(updates.getStatus());
        if (updates.getMaxWeight() != null) existing.setMaxWeight(updates.getMaxWeight());
        if (updates.getMaxCube() != null) existing.setMaxCube(updates.getMaxCube());
        // Add more fields as needed
    }

    private LocationValidationFacts buildValidationFacts(Location location, MasterDataPluginContext context, String operation) {
        return LocationValidationFacts.builder()
                .clientCode(context.getClientCode())
                .facilityCode(context.getFacilityCode())
                .operationType(operation)
                .locationCode(location.getLocationCode())
                .locationType(location.getLocationType() != null ? location.getLocationType().name() : null)
                .zone(location.getZone())
                .aisle(location.getAisle())
                .bay(location.getBay())
                .level(location.getLevel())
                .position(location.getPosition())
                .length(location.getLength())
                .width(location.getWidth())
                .height(location.getHeight())
                .maxWeight(location.getMaxWeight())
                .maxCube(location.getMaxCube())
                .maxPallets(location.getMaxPallets())
                .maxCases(location.getMaxCases())
                .maxEaches(location.getMaxEaches())
                .pickLocation(location.isPickLocation())
                .putawayLocation(location.isPutawayLocation())
                .mixedSku(location.isMixedSku())
                .mixedLot(location.isMixedLot())
                .clientConfig(context.getParameters())
                .build();
    }
}
