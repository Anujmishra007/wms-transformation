package com.maersk.wms.inventory.api;

import com.maersk.wms.inventory.api.dto.*;
import com.maersk.wms.inventory.domain.*;
import com.maersk.wms.inventory.plugin.context.InventoryPluginContext;
import com.maersk.wms.inventory.service.InventoryService;
import com.maersk.wms.inventory.variation.InventoryVariationResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST API for Inventory operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory API", description = "Inventory Management Operations")
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryVariationResolver variationResolver;

    @GetMapping("/{lotxLocxIdKey}")
    @Operation(summary = "Get inventory by key")
    public ResponseEntity<InventoryResponse> getInventory(@PathVariable String lotxLocxIdKey) {
        log.info("GET /inventory/{}", lotxLocxIdKey);
        return inventoryService.getInventory(lotxLocxIdKey)
                .map(inv -> ResponseEntity.ok(InventoryResponse.from(inv)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sku/{sku}/available")
    @Operation(summary = "Get available inventory for SKU")
    public ResponseEntity<List<InventoryResponse>> getAvailableInventory(
            @PathVariable String sku,
            @RequestHeader("X-Warehouse-Code") String warehouseCode) {
        log.info("GET /inventory/sku/{}/available", sku);
        List<LotxLocxId> inventory = inventoryService.getAvailableInventory(sku, warehouseCode);
        return ResponseEntity.ok(inventory.stream().map(InventoryResponse::from).toList());
    }

    @PostMapping("/adjustments")
    @Operation(summary = "Create inventory adjustment")
    public ResponseEntity<AdjustmentResponse> createAdjustment(
            @Valid @RequestBody AdjustmentRequest request,
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestHeader("X-Country-Code") String countryCode,
            @RequestHeader("X-Warehouse-Code") String warehouseCode,
            @RequestHeader("X-User-Id") String userId) {

        log.info("POST /inventory/adjustments - SKU={}, location={}", request.getSku(), request.getLocation());

        InventoryPluginContext context = variationResolver.resolveContext(clientCode, countryCode, warehouseCode, userId);

        InventoryAdjustment adjustment = InventoryAdjustment.builder()
                .sku(request.getSku())
                .location(request.getLocation())
                .lpn(request.getLpn())
                .lot(request.getLot())
                .adjustmentType(AdjustmentType.fromCode(request.getAdjustmentType()))
                .systemQty(request.getSystemQty())
                .adjustedQty(request.getAdjustedQty())
                .reasonCode(request.getReasonCode())
                .comments(request.getComments())
                .userId(userId)
                .build();

        InventoryAdjustment result = inventoryService.processAdjustment(adjustment, context);
        return ResponseEntity.ok(AdjustmentResponse.from(result));
    }

    @PostMapping("/transfers")
    @Operation(summary = "Create inventory transfer")
    public ResponseEntity<TransferResponse> createTransfer(
            @Valid @RequestBody TransferRequest request,
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestHeader("X-Country-Code") String countryCode,
            @RequestHeader("X-Warehouse-Code") String warehouseCode,
            @RequestHeader("X-User-Id") String userId) {

        log.info("POST /inventory/transfers - from={} to={}", request.getFromLocation(), request.getToLocation());

        InventoryPluginContext context = variationResolver.resolveContext(clientCode, countryCode, warehouseCode, userId);

        InventoryTransfer transfer = InventoryTransfer.builder()
                .sku(request.getSku())
                .lot(request.getLot())
                .fromLocation(request.getFromLocation())
                .fromLpn(request.getFromLpn())
                .toLocation(request.getToLocation())
                .toLpn(request.getToLpn())
                .transferQty(request.getTransferQty())
                .transferType(TransferType.fromCode(request.getTransferType()))
                .userId(userId)
                .build();

        InventoryTransfer result = inventoryService.processTransfer(transfer, context);
        return ResponseEntity.ok(TransferResponse.from(result));
    }

    @PostMapping("/holds")
    @Operation(summary = "Apply inventory hold")
    public ResponseEntity<HoldResponse> applyHold(
            @Valid @RequestBody HoldRequest request,
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestHeader("X-Country-Code") String countryCode,
            @RequestHeader("X-Warehouse-Code") String warehouseCode,
            @RequestHeader("X-User-Id") String userId) {

        log.info("POST /inventory/holds - code={}, scope={}", request.getHoldCode(), request.getScope());

        InventoryPluginContext context = variationResolver.resolveContext(clientCode, countryCode, warehouseCode, userId);

        InventoryHold hold = InventoryHold.builder()
                .holdCode(request.getHoldCode())
                .scope(HoldScope.valueOf(request.getScope()))
                .sku(request.getSku())
                .lot(request.getLot())
                .location(request.getLocation())
                .lpn(request.getLpn())
                .reasonCode(request.getReasonCode())
                .comments(request.getComments())
                .holdBy(userId)
                .build();

        InventoryHold result = inventoryService.applyHold(hold, context);
        return ResponseEntity.ok(HoldResponse.from(result));
    }

    @DeleteMapping("/holds/{holdKey}")
    @Operation(summary = "Release inventory hold")
    public ResponseEntity<Void> releaseHold(
            @PathVariable String holdKey,
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestHeader("X-Country-Code") String countryCode,
            @RequestHeader("X-Warehouse-Code") String warehouseCode,
            @RequestHeader("X-User-Id") String userId) {

        log.info("DELETE /inventory/holds/{}", holdKey);

        InventoryPluginContext context = variationResolver.resolveContext(clientCode, countryCode, warehouseCode, userId);

        InventoryHold hold = InventoryHold.builder()
                .holdKey(holdKey)
                .releaseBy(userId)
                .build();

        inventoryService.releaseHold(hold, context);
        return ResponseEntity.ok().build();
    }
}
