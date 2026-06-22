package com.maersk.wms.inventory.acl.inbound;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Adapter implementation for Inbound Operations Service facade.
 * Communicates with inbound-operations-service via REST/gRPC.
 */
@Component
public class InboundOperationsAdapter implements InboundOperationsFacade {

    private final WebClient inboundClient;

    public InboundOperationsAdapter(WebClient.Builder webClientBuilder) {
        this.inboundClient = webClientBuilder
                .baseUrl("${services.inbound-operations.url:http://inbound-operations-service}")
                .build();
    }

    // ═══════════════════════════════════════════════════════════════
    // RECEIPT QUERIES
    // ═══════════════════════════════════════════════════════════════

    @Override
    public Optional<ReceiptInfo> getReceiptInfo(ReceiptKey receiptKey) {
        // TODO: Implement REST call to inbound-operations-service
        // GET /api/v1/receipts/{receiptKey}
        return Optional.empty();
    }

    @Override
    public Optional<ReceiptLineInfo> getReceiptLineInfo(ReceiptKey receiptKey, String lineNumber) {
        // TODO: Implement REST call
        // GET /api/v1/receipts/{receiptKey}/lines/{lineNumber}
        return Optional.empty();
    }

    @Override
    public List<ReceiptLineInfo> getReceiptLinesReadyForPutaway(ReceiptKey receiptKey) {
        // TODO: Implement REST call
        // GET /api/v1/receipts/{receiptKey}/lines?status=READY_FOR_PUTAWAY
        return Collections.emptyList();
    }

    @Override
    public boolean isReceiptComplete(ReceiptKey receiptKey) {
        // TODO: Implement REST call
        // GET /api/v1/receipts/{receiptKey}/status
        return false;
    }

    // ═══════════════════════════════════════════════════════════════
    // RETURN QUERIES
    // ═══════════════════════════════════════════════════════════════

    @Override
    public Optional<ReturnInfo> getReturnInfo(String returnKey) {
        // TODO: Implement REST call
        // GET /api/v1/returns/{returnKey}
        return Optional.empty();
    }

    @Override
    public Optional<ReturnLineInfo> getReturnLineInfo(String returnKey, String lineNumber) {
        // TODO: Implement REST call
        // GET /api/v1/returns/{returnKey}/lines/{lineNumber}
        return Optional.empty();
    }

    // ═══════════════════════════════════════════════════════════════
    // LOT QUERIES
    // ═══════════════════════════════════════════════════════════════

    @Override
    public LotKey getOrCreateLot(ReceiptKey receiptKey, SkuKey skuKey, LottableAttributes lottables) {
        // TODO: Implement REST call
        // POST /api/v1/lots
        return new LotKey("");
    }

    @Override
    public Optional<LotInfo> getLotInfoFromReceipt(ReceiptKey receiptKey, String lineNumber) {
        // TODO: Implement REST call
        // GET /api/v1/receipts/{receiptKey}/lines/{lineNumber}/lot
        return Optional.empty();
    }

    // ═══════════════════════════════════════════════════════════════
    // LPN QUERIES
    // ═══════════════════════════════════════════════════════════════

    @Override
    public Optional<LpnKey> getLpnFromReceipt(ReceiptKey receiptKey, String lineNumber) {
        // TODO: Implement REST call
        // GET /api/v1/receipts/{receiptKey}/lines/{lineNumber}/lpn
        return Optional.empty();
    }

    @Override
    public LpnKey getOrGenerateLpn(ReceiptKey receiptKey, String lineNumber) {
        // TODO: Implement REST call
        // POST /api/v1/lpns/generate
        return new LpnKey("");
    }

    // ═══════════════════════════════════════════════════════════════
    // NOTIFICATION CALLBACKS
    // ═══════════════════════════════════════════════════════════════

    @Override
    public void notifyInventoryCreated(ReceiptKey receiptKey, String lineNumber, InventoryKey inventoryKey) {
        // TODO: Implement REST call or event publish
        // POST /api/v1/receipts/{receiptKey}/lines/{lineNumber}/inventory-created
        // OR publish InventoryCreatedEvent to Kafka
    }

    @Override
    public void notifyPutawayComplete(ReceiptKey receiptKey, String lineNumber, LocationKey locationKey) {
        // TODO: Implement REST call or event publish
        // POST /api/v1/receipts/{receiptKey}/lines/{lineNumber}/putaway-complete
        // OR publish PutawayCompleteEvent to Kafka
    }
}
