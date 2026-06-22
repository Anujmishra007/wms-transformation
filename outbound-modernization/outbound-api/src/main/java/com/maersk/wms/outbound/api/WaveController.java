package com.maersk.wms.outbound.api;

import com.maersk.wms.outbound.domain.Wave;
import com.maersk.wms.outbound.domain.WaveStatus;
import com.maersk.wms.outbound.plugin.OutboundPluginContext;
import com.maersk.wms.outbound.service.WaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST API for wave operations.
 */
@RestController
@RequestMapping("/api/v1/outbound/waves")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Waves", description = "Wave planning and management APIs")
public class WaveController {

    private final WaveService waveService;

    @PostMapping
    @Operation(summary = "Create a new wave")
    public ResponseEntity<WaveResponse> createWave(
            @Valid @RequestBody CreateWaveRequest request,
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestHeader("X-Facility-Code") String facilityCode,
            @RequestHeader("X-User-Id") String userId) {

        OutboundPluginContext context = OutboundPluginContext.builder()
                .clientCode(clientCode)
                .facilityCode(facilityCode)
                .userId(userId)
                .build();

        Wave wave = waveService.createWave(request.getOrderNumbers(), request.getWaveType(), context);

        return ResponseEntity.ok(WaveResponse.fromEntity(wave));
    }

    @GetMapping("/{waveNumber}")
    @Operation(summary = "Get wave by wave number")
    public ResponseEntity<WaveResponse> getWave(@PathVariable String waveNumber) {
        return waveService.getWave(waveNumber)
                .map(WaveResponse::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get waves by status")
    public ResponseEntity<List<WaveResponse>> getWavesByStatus(@RequestParam WaveStatus status) {
        List<WaveResponse> waves = waveService.getWavesByStatus(status)
                .stream()
                .map(WaveResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(waves);
    }

    @PostMapping("/{waveNumber}/release")
    @Operation(summary = "Release wave for processing")
    public ResponseEntity<WaveResponse> releaseWave(
            @PathVariable String waveNumber,
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestHeader("X-Facility-Code") String facilityCode,
            @RequestHeader("X-User-Id") String userId) {

        OutboundPluginContext context = OutboundPluginContext.builder()
                .clientCode(clientCode)
                .facilityCode(facilityCode)
                .userId(userId)
                .build();

        Wave released = waveService.releaseWave(waveNumber, context);

        return ResponseEntity.ok(WaveResponse.fromEntity(released));
    }

    @Data
    public static class CreateWaveRequest {
        @NotEmpty(message = "At least one order number is required")
        private List<String> orderNumbers;

        private String waveType = "STANDARD";
    }

    @Data
    @lombok.Builder
    public static class WaveResponse {
        private String waveKey;
        private String waveType;
        private String status;
        private int totalOrders;
        private int totalLines;
        private java.math.BigDecimal totalQty;
        private String createdBy;
        private String addDate;
        private String releasedBy;
        private String actualStartTime;

        public static WaveResponse fromEntity(Wave wave) {
            return WaveResponse.builder()
                    .waveKey(wave.getWaveKey())
                    .waveType(wave.getWaveType())
                    .status(wave.getStatus() != null ? wave.getStatus().name() : null)
                    .totalOrders(wave.getTotalOrders())
                    .totalLines(wave.getTotalLines())
                    .totalQty(wave.getTotalQty())
                    .createdBy(wave.getCreatedBy())
                    .addDate(wave.getAddDate() != null ? wave.getAddDate().toString() : null)
                    .releasedBy(wave.getReleasedBy())
                    .actualStartTime(wave.getActualStartTime() != null ? wave.getActualStartTime().toString() : null)
                    .build();
        }
    }
}
