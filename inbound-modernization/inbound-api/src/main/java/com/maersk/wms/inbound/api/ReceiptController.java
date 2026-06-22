package com.maersk.wms.inbound.api;

import com.maersk.wms.inbound.api.dto.*;
import com.maersk.wms.inbound.domain.operations_service.Receipt;
import com.maersk.wms.inbound.domain.operations_service.ReceiptStatus;
import com.maersk.wms.inbound.service.operations_service.ReceivingService;
import com.maersk.wms.inbound.service.operations_service.dto.CreateReceiptRequest;
import com.maersk.wms.inbound.shared.kernel.identifiers.ReceiptKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
            @Valid @RequestBody com.maersk.wms.inbound.api.dto.CreateReceiptRequest request) {

        log.info("Creating receipt for client: {}", clientCode);

        CreateReceiptRequest serviceRequest = new CreateReceiptRequest();
        serviceRequest.setStorerKey(request.getStorerKey());
        serviceRequest.setPoKey(request.getPoKey());
        serviceRequest.setAsnKey(request.getAsnKey());
        serviceRequest.setCreatedBy(userId);

        Receipt created = receivingService.createReceipt(serviceRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(created));
    }

    @GetMapping("/{receiptKey}")
    @Operation(summary = "Get receipt by key")
    public ResponseEntity<ReceiptResponse> getReceipt(
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String receiptKey) {

        return receivingService.getReceipt(ReceiptKey.of(receiptKey))
                .map(receipt -> ResponseEntity.ok(mapToResponse(receipt)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "List receipts by status")
    public ResponseEntity<List<ReceiptResponse>> listReceipts(
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String storerKey) {

        List<Receipt> receipts;
        if (status != null) {
            receipts = receivingService.getByStatus(ReceiptStatus.valueOf(status));
        } else if (storerKey != null) {
            receipts = receivingService.getByStorer(storerKey);
        } else {
            // Default to open receipts
            receipts = receivingService.getByStatus(ReceiptStatus.OPEN);
        }

        return ResponseEntity.ok(receipts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()));
    }

    @PostMapping("/{receiptKey}/start")
    @Operation(summary = "Start receiving for a receipt")
    public ResponseEntity<ReceiptResponse> startReceiving(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String receiptKey) {

        receivingService.startReceiving(ReceiptKey.of(receiptKey), userId);
        return receivingService.getReceipt(ReceiptKey.of(receiptKey))
                .map(receipt -> ResponseEntity.ok(mapToResponse(receipt)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{receiptKey}/complete")
    @Operation(summary = "Complete receiving for a receipt")
    public ResponseEntity<ReceiptResponse> completeReceiving(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String receiptKey) {

        receivingService.completeReceiving(ReceiptKey.of(receiptKey), userId);
        return receivingService.getReceipt(ReceiptKey.of(receiptKey))
                .map(receipt -> ResponseEntity.ok(mapToResponse(receipt)))
                .orElse(ResponseEntity.notFound().build());
    }

    private ReceiptResponse mapToResponse(Receipt receipt) {
        return ReceiptResponse.builder()
                .receiptKey(receipt.getReceiptKey() != null ? receipt.getReceiptKey().getValue() : null)
                .storerKey(receipt.getStorerKey() != null ? receipt.getStorerKey().getValue() : null)
                .poKey(receipt.getPoKey())
                .asnKey(receipt.getAsnKey())
                .status(receipt.getStatus() != null ? receipt.getStatus().name() : null)
                .statusDescription(receipt.getStatus() != null ? receipt.getStatus().getDescription() : null)
                .totalReceivedQty(receipt.getReceivedQty())
                .totalExpectedQty(receipt.getExpectedQty())
                .build();
    }
}
