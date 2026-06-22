package com.maersk.wms.masterdata.api;

import com.maersk.wms.masterdata.domain.Location;
import com.maersk.wms.masterdata.domain.LocationType;
import com.maersk.wms.masterdata.plugin.MasterDataPluginContext;
import com.maersk.wms.masterdata.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST API for location master data operations.
 */
@RestController
@RequestMapping("/api/v1/masterdata/locations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Locations", description = "Location master data APIs")
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    @Operation(summary = "Create a new location")
    public ResponseEntity<LocationResponse> createLocation(
            @Valid @RequestBody CreateLocationRequest request,
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestHeader("X-Facility-Code") String facilityCode,
            @RequestHeader("X-User-Id") String userId) {

        MasterDataPluginContext context = MasterDataPluginContext.builder()
                .clientCode(clientCode)
                .facilityCode(facilityCode)
                .userId(userId)
                .build();

        Location location = request.toEntity();
        Location created = locationService.createLocation(location, context);

        return ResponseEntity.ok(LocationResponse.fromEntity(created));
    }

    @GetMapping("/{locationCode}")
    @Operation(summary = "Get location by code")
    public ResponseEntity<LocationResponse> getLocation(@PathVariable String locationCode) {
        return locationService.getLocation(locationCode)
                .map(LocationResponse::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get locations by zone or type")
    public ResponseEntity<List<LocationResponse>> getLocations(
            @RequestParam(required = false) String zone,
            @RequestParam(required = false) LocationType type) {

        List<Location> locations;
        if (zone != null && !zone.isEmpty()) {
            locations = locationService.getLocationsByZone(zone);
        } else if (type != null) {
            locations = locationService.getLocationsByType(type);
        } else {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(locations.stream()
                .map(LocationResponse::fromEntity)
                .collect(Collectors.toList()));
    }

    @PostMapping("/generate-code")
    @Operation(summary = "Generate location code")
    public ResponseEntity<String> generateLocationCode(
            @RequestBody GenerateLocationCodeRequest request,
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestHeader("X-Facility-Code") String facilityCode,
            @RequestHeader("X-User-Id") String userId) {

        MasterDataPluginContext context = MasterDataPluginContext.builder()
                .clientCode(clientCode)
                .facilityCode(facilityCode)
                .userId(userId)
                .build();

        String code = locationService.generateLocationCode(
                request.getZone(), request.getAisle(), request.getBay(),
                request.getLevel(), request.getPosition(), context);

        return ResponseEntity.ok(code);
    }

    @Data
    public static class CreateLocationRequest {
        @NotBlank private String locationCode;
        private String description;
        private String locationType;
        private String zone;
        private String aisle;
        private String bay;
        private String level;
        private String position;
        private BigDecimal length;
        private BigDecimal width;
        private BigDecimal height;
        private BigDecimal maxWeight;
        private BigDecimal maxCube;
        private int maxPallets;
        private int maxCases;
        private boolean pickLocation;
        private boolean putawayLocation;
        private boolean mixedSku;
        private boolean mixedLot;

        public Location toEntity() {
            Location loc = new Location();
            loc.setLocationCode(locationCode);
            loc.setDescription(description);
            loc.setLocationType(locationType != null ? LocationType.valueOf(locationType) : LocationType.RESERVE);
            loc.setZone(zone);
            loc.setAisle(aisle);
            loc.setBay(bay);
            loc.setLevel(level);
            loc.setPosition(position);
            loc.setLength(length);
            loc.setWidth(width);
            loc.setHeight(height);
            loc.setMaxWeight(maxWeight);
            loc.setMaxCube(maxCube);
            loc.setMaxPallets(maxPallets);
            loc.setMaxCases(maxCases);
            loc.setPickLocation(pickLocation);
            loc.setPutawayLocation(putawayLocation);
            loc.setMixedSku(mixedSku);
            loc.setMixedLot(mixedLot);
            return loc;
        }
    }

    @Data
    public static class GenerateLocationCodeRequest {
        private String zone;
        private String aisle;
        private String bay;
        private String level;
        private String position;
    }

    @Data
    @lombok.Builder
    public static class LocationResponse {
        private Long id;
        private String locationCode;
        private String description;
        private String locationType;
        private String status;
        private String zone;
        private String aisle;
        private String bay;
        private String level;
        private String position;
        private BigDecimal maxWeight;
        private BigDecimal maxCube;
        private int maxPallets;
        private boolean pickLocation;
        private boolean putawayLocation;
        private int pickPathSequence;

        public static LocationResponse fromEntity(Location loc) {
            return LocationResponse.builder()
                    .id(loc.getId())
                    .locationCode(loc.getLocationCode())
                    .description(loc.getDescription())
                    .locationType(loc.getLocationType() != null ? loc.getLocationType().name() : null)
                    .status(loc.getStatus() != null ? loc.getStatus().name() : null)
                    .zone(loc.getZone())
                    .aisle(loc.getAisle())
                    .bay(loc.getBay())
                    .level(loc.getLevel())
                    .position(loc.getPosition())
                    .maxWeight(loc.getMaxWeight())
                    .maxCube(loc.getMaxCube())
                    .maxPallets(loc.getMaxPallets())
                    .pickLocation(loc.isPickLocation())
                    .putawayLocation(loc.isPutawayLocation())
                    .pickPathSequence(loc.getPickPathSequence())
                    .build();
        }
    }
}
