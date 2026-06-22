package com.maersk.wms.inbound.api;

import com.maersk.wms.inbound.api.dto.*;
import com.maersk.wms.inbound.domain.Receipt;
import com.maersk.wms.inbound.domain.ReceiptDetail;
import com.maersk.wms.inbound.domain.ReceiptStatus;
import com.maersk.wms.inbound.plugin.InboundPluginContext;
import com.maersk.wms.inbound.service.ReceivingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for receipt operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/receipts")
@RequiredArgsConstructor
@Tag(name = "Receipts", description = "Receipt management operations")
public class ReceiptController {

    private final ReceivingService receivingService;

    @PostMapping
    @Operation(summary = "Create a new receipt")
    public ResponseEntity<ReceiptResponse> createReceipt(
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestHeader("X-Country-Code") String countryCode,
            @RequestHeader("X-Warehouse-Code") String warehouseCode,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateReceiptRequest request) {

        log.info("Creating receipt for client: {}", clientCode);

        InboundPluginContext context = buildContext(clientCode, countryCode, warehouseCode, userId);
        Receipt receipt = mapToReceipt(request);
        Receipt created = receivingService.createReceipt(receipt, context);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(created));
    }

    @GetMapping("/{receiptKey}")
    @Operation(summary = "Get receipt by key")
    public ResponseEntity<ReceiptResponse> getReceipt(@PathVariable String receiptKey) {
        Receipt receipt = receivingService.getReceipt(receiptKey);
        return ResponseEntity.ok(mapToResponse(receipt));
    }

    @GetMapping
    @Operation(summary = "Find receipts by status")
    public ResponseEntity<List<ReceiptResponse>> findByStatus(
            @RequestParam(required = false) String status) {

        List<Receipt> receipts = status != null
                ? receivingService.findByStatus(ReceiptStatus.fromCode(status))
                : List.of();

        List<ReceiptResponse> responses = receipts.stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{receiptKey}/receive")
    @Operation(summary = "Receive inventory for a receipt line")
    public ResponseEntity<ReceiptDetailResponse> receiveInventory(
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestHeader("X-Country-Code") String countryCode,
            @RequestHeader("X-Warehouse-Code") String warehouseCode,
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String receiptKey,
            @Valid @RequestBody ReceiveInventoryRequest request) {

        log.info("Receiving inventory for receipt: {}", receiptKey);

        InboundPluginContext context = buildContext(clientCode, countryCode, warehouseCode, userId);
        ReceiptDetail detail = mapToDetail(request);
        ReceiptDetail received = receivingService.receiveInventory(receiptKey, detail, context);

        return ResponseEntity.ok(mapToDetailResponse(received));
    }

    @PostMapping("/{receiptKey}/close")
    @Operation(summary = "Close a receipt")
    public ResponseEntity<ReceiptResponse> closeReceipt(
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestHeader("X-Country-Code") String countryCode,
            @RequestHeader("X-Warehouse-Code") String warehouseCode,
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String receiptKey) {

        log.info("Closing receipt: {}", receiptKey);

        InboundPluginContext context = buildContext(clientCode, countryCode, warehouseCode, userId);
        Receipt closed = receivingService.closeReceipt(receiptKey, context);

        return ResponseEntity.ok(mapToResponse(closed));
    }

    private InboundPluginContext buildContext(String clientCode, String countryCode,
                                               String warehouseCode, String userId) {
        return InboundPluginContext.builder()
                .clientCode(clientCode)
                .countryCode(countryCode)
                .warehouseCode(warehouseCode)
                .userId(userId)
                .build();
    }

    private Receipt mapToReceipt(CreateReceiptRequest request) {
        return Receipt.builder()
                .externalReceiptKey(request.getExternalReceiptKey())
                .storerKey(request.getStorerKey())
                .receiptType(request.getReceiptType())
                .poKey(request.getPoKey())
                .asnKey(request.getAsnKey())
                .carrierKey(request.getCarrierKey())
                .trailerNumber(request.getTrailerNumber())
                .sealNumber(request.getSealNumber())
                .door(request.getDoor())
                .expectedArrivalDate(request.getExpectedArrivalDate())
                .notes(request.getNotes())
                .build();
    }

    private ReceiptDetail mapToDetail(ReceiveInventoryRequest request) {
        return ReceiptDetail.builder()
                .sku(request.getSku())
                .lot(request.getLot())
                .id(request.getLpn())
                .receivedQty(request.getReceivedQty())
                .damagedQty(request.getDamagedQty())
                .location(request.getLocation())
                .conditionCode(request.getConditionCode())
                .expirationDate(request.getExpirationDate())
                .build();
    }

    private ReceiptResponse mapToResponse(Receipt receipt) {
        return ReceiptResponse.builder()
                .receiptKey(receipt.getReceiptKey())
                .externalReceiptKey(receipt.getExternalReceiptKey())
                .storerKey(receipt.getStorerKey())
                .receiptType(receipt.getReceiptType())
                .status(receipt.getStatus().getCode())
                .statusDescription(receipt.getStatus().getDescription())
                .poKey(receipt.getPoKey())
                .asnKey(receipt.getAsnKey())
                .totalExpectedQty(receipt.getTotalExpectedQty())
                .totalReceivedQty(receipt.getTotalReceivedQty())
                .totalDamagedQty(receipt.getTotalDamagedQty())
                .variance(receipt.getTotalVariance())
                .build();
    }

    private ReceiptDetailResponse mapToDetailResponse(ReceiptDetail detail) {
        return ReceiptDetailResponse.builder()
                .receiptKey(detail.getReceiptKey())
                .lineNumber(detail.getReceiptLineNumber())
                .sku(detail.getSku())
                .lot(detail.getLot())
                .lpn(detail.getId())
                .receivedQty(detail.getReceivedQty())
                .damagedQty(detail.getDamagedQty())
                .location(detail.getLocation())
                .status(detail.getStatus().getCode())
                .build();
    }
}
