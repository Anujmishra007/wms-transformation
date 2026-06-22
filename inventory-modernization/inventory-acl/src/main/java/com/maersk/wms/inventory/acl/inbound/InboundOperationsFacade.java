package com.maersk.wms.inventory.acl.inbound;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Anti-Corruption Layer facade for Inbound Operations Service.
 * Translates inbound domain concepts to inventory domain.
 * Upstream service providing receipt and return data.
 */
public interface InboundOperationsFacade {

    // ═══════════════════════════════════════════════════════════════
    // RECEIPT QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Get receipt details for inventory creation.
     */
    Optional<ReceiptInfo> getReceiptInfo(ReceiptKey receiptKey);

    /**
     * Get receipt line details.
     */
    Optional<ReceiptLineInfo> getReceiptLineInfo(ReceiptKey receiptKey, String lineNumber);

    /**
     * Get all receipt lines ready for putaway.
     */
    List<ReceiptLineInfo> getReceiptLinesReadyForPutaway(ReceiptKey receiptKey);

    /**
     * Check if receipt is complete.
     */
    boolean isReceiptComplete(ReceiptKey receiptKey);

    // ═══════════════════════════════════════════════════════════════
    // RETURN QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Get return details for inventory creation.
     */
    Optional<ReturnInfo> getReturnInfo(String returnKey);

    /**
     * Get return line details.
     */
    Optional<ReturnLineInfo> getReturnLineInfo(String returnKey, String lineNumber);

    // ═══════════════════════════════════════════════════════════════
    // LOT QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Get or create lot from receipt.
     */
    LotKey getOrCreateLot(ReceiptKey receiptKey, SkuKey skuKey, LottableAttributes lottables);

    /**
     * Get lot info from receipt.
     */
    Optional<LotInfo> getLotInfoFromReceipt(ReceiptKey receiptKey, String lineNumber);

    // ═══════════════════════════════════════════════════════════════
    // LPN QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Get LPN from receipt.
     */
    Optional<LpnKey> getLpnFromReceipt(ReceiptKey receiptKey, String lineNumber);

    /**
     * Get or generate LPN for receipt line.
     */
    LpnKey getOrGenerateLpn(ReceiptKey receiptKey, String lineNumber);

    // ═══════════════════════════════════════════════════════════════
    // NOTIFICATION CALLBACKS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Notify inbound that inventory has been created.
     */
    void notifyInventoryCreated(ReceiptKey receiptKey, String lineNumber, InventoryKey inventoryKey);

    /**
     * Notify inbound that putaway is complete.
     */
    void notifyPutawayComplete(ReceiptKey receiptKey, String lineNumber, LocationKey locationKey);

    // ═══════════════════════════════════════════════════════════════
    // DTOs
    // ═══════════════════════════════════════════════════════════════

    record ReceiptInfo(
            ReceiptKey receiptKey,
            String receiptNumber,
            StorerKey storerKey,
            WarehouseKey warehouseKey,
            String receiptType,
            String status,
            Instant receiptDate,
            String externalReference,
            int totalLines
    ) {}

    record ReceiptLineInfo(
            ReceiptKey receiptKey,
            String lineNumber,
            SkuKey skuKey,
            Quantity expectedQuantity,
            Quantity receivedQuantity,
            LotKey lotKey,
            LpnKey lpnKey,
            LottableAttributes lottables,
            String status,
            LocationKey suggestedLocation
    ) {}

    record ReturnInfo(
            String returnKey,
            OrderKey originalOrderKey,
            StorerKey storerKey,
            WarehouseKey warehouseKey,
            String returnType,
            String status,
            Instant returnDate
    ) {}

    record ReturnLineInfo(
            String returnKey,
            String lineNumber,
            SkuKey skuKey,
            Quantity returnedQuantity,
            String returnReason,
            String condition,
            LottableAttributes lottables
    ) {}

    record LotInfo(
            LotKey lotKey,
            SkuKey skuKey,
            LottableAttributes lottables,
            Instant expiryDate,
            Instant manufactureDate
    ) {}
}
