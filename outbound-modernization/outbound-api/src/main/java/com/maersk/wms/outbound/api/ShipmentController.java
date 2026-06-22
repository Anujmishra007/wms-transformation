package com.maersk.wms.outbound.api;

import com.maersk.wms.outbound.domain.Shipment;
import com.maersk.wms.outbound.domain.ShipmentStatus;
import com.maersk.wms.outbound.plugin.OutboundPluginContext;
import com.maersk.wms.outbound.service.ShipmentService;
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
 * REST API for shipment operations.
 */
@RestController
@RequestMapping("/api/v1/outbound/shipments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Shipments", description = "Shipment management APIs")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping
    @Operation(summary = "Create a new shipment")
    public ResponseEntity<ShipmentResponse> createShipment(
            @Valid @RequestBody CreateShipmentRequest request,
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestHeader("X-Facility-Code") String facilityCode,
            @RequestHeader("X-User-Id") String userId) {

        OutboundPluginContext context = OutboundPluginContext.builder()
                .clientCode(clientCode)
                .facilityCode(facilityCode)
                .userId(userId)
                .build();

        Shipment shipment = shipmentService.createShipment(request.getOrderNumber(), context);

        return ResponseEntity.ok(ShipmentResponse.fromEntity(shipment));
    }

    @GetMapping("/{shipmentId}")
    @Operation(summary = "Get shipment by ID")
    public ResponseEntity<ShipmentResponse> getShipment(@PathVariable String shipmentId) {
        return shipmentService.getShipment(shipmentId)
                .map(ShipmentResponse::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get shipments by status")
    public ResponseEntity<List<ShipmentResponse>> getShipmentsByStatus(@RequestParam ShipmentStatus status) {
        List<ShipmentResponse> shipments = shipmentService.getShipmentsByStatus(status)
                .stream()
                .map(ShipmentResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(shipments);
    }

    @PostMapping("/{shipmentId}/confirm")
    @Operation(summary = "Confirm shipment (ship confirm)")
    public ResponseEntity<ShipmentResponse> confirmShipment(
            @PathVariable String shipmentId,
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestHeader("X-Facility-Code") String facilityCode,
            @RequestHeader("X-User-Id") String userId) {

        OutboundPluginContext context = OutboundPluginContext.builder()
                .clientCode(clientCode)
                .facilityCode(facilityCode)
                .userId(userId)
                .build();

        Shipment confirmed = shipmentService.confirmShipment(shipmentId, context);

        return ResponseEntity.ok(ShipmentResponse.fromEntity(confirmed));
    }

    @PostMapping("/{shipmentId}/manifest")
    @Operation(summary = "Generate manifest for shipment")
    public ResponseEntity<ShipmentResponse> generateManifest(
            @PathVariable String shipmentId,
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestHeader("X-Facility-Code") String facilityCode,
            @RequestHeader("X-User-Id") String userId) {

        OutboundPluginContext context = OutboundPluginContext.builder()
                .clientCode(clientCode)
                .facilityCode(facilityCode)
                .userId(userId)
                .build();

        Shipment manifested = shipmentService.generateManifest(shipmentId, context);

        return ResponseEntity.ok(ShipmentResponse.fromEntity(manifested));
    }

    @Data
    public static class CreateShipmentRequest {
        @NotBlank(message = "Order number is required")
        private String orderNumber;

        private String carrier;
        private String shipMethod;
    }

    @Data
    @lombok.Builder
    public static class ShipmentResponse {
        private Long id;
        private String shipmentId;
        private String orderNumber;
        private String status;
        private String shipmentType;
        private String carrierCode;
        private String shipMethod;
        private String trackingNumber;
        private BigDecimal totalWeight;
        private BigDecimal freightCharge;
        private int cartonCount;

        private String shipToName;
        private String shipToCity;
        private String shipToState;
        private String shipToCountry;
        private String shipToZip;

        private String createdBy;
        private String createdAt;
        private String shippedBy;
        private String shippedAt;

        public static ShipmentResponse fromEntity(Shipment shipment) {
            return ShipmentResponse.builder()
                    .id(shipment.getId())
                    .shipmentId(shipment.getShipmentId())
                    .orderNumber(shipment.getOrderNumber())
                    .status(shipment.getStatus() != null ? shipment.getStatus().name() : null)
                    .shipmentType(shipment.getShipmentType() != null ? shipment.getShipmentType().name() : null)
                    .carrierCode(shipment.getCarrierCode())
                    .shipMethod(shipment.getShipMethod())
                    .trackingNumber(shipment.getTrackingNumber())
                    .totalWeight(shipment.getTotalWeight())
                    .freightCharge(shipment.getFreightCharge())
                    .cartonCount(shipment.getCartonCount())
                    .shipToName(shipment.getShipToName())
                    .shipToCity(shipment.getShipToCity())
                    .shipToState(shipment.getShipToState())
                    .shipToCountry(shipment.getShipToCountry())
                    .shipToZip(shipment.getShipToZip())
                    .createdBy(shipment.getCreatedBy())
                    .createdAt(shipment.getCreatedAt() != null ? shipment.getCreatedAt().toString() : null)
                    .shippedBy(shipment.getShippedBy())
                    .shippedAt(shipment.getShippedAt() != null ? shipment.getShippedAt().toString() : null)
                    .build();
        }
    }
}
