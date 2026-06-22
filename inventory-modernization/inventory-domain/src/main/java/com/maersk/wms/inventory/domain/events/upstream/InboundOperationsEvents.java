package com.maersk.wms.inventory.domain.events.upstream;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Upstream events consumed FROM inbound-operations-service.
 * These events trigger inventory creation and updates.
 */
public final class InboundOperationsEvents {

    private InboundOperationsEvents() {}

    // ═══════════════════════════════════════════════════════════════
    // RECEIVING EVENTS - Trigger inventory creation
    // ═══════════════════════════════════════════════════════════════

    public record ReceiptLineCompleted(
            String eventId,
            ReceiptKey receiptKey,
            String receiptLineNumber,
            SkuKey skuKey,
            StorerKey storerKey,
            Quantity receivedQuantity,
            String uom,
            LpnKey lpnKey,
            LocationKey stagingLocationKey,
            LottableAttributes lottables,
            LocalDateTime receiptDate,
            UserKey receivedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) {}

    public record ReceiptFinalized(
            String eventId,
            ReceiptKey receiptKey,
            String asnKey,
            StorerKey storerKey,
            int totalLines,
            Quantity totalQuantity,
            int lpnsCreated,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) {}

    // ═══════════════════════════════════════════════════════════════
    // RETURNS RECEIPT EVENTS - Trigger return inventory creation
    // ═══════════════════════════════════════════════════════════════

    public record ReturnReceiptLineCompleted(
            String eventId,
            String returnKey,
            String returnLineNumber,
            OrderKey originalOrderKey,
            SkuKey skuKey,
            StorerKey storerKey,
            Quantity returnedQuantity,
            LpnKey lpnKey,
            LocationKey locationKey,
            String returnReason,
            String disposition,         // RESTOCK, QC_REQUIRED, DAMAGE, DISPOSE
            UserKey receivedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) {}

    // ═══════════════════════════════════════════════════════════════
    // PUTAWAY EVENTS - Trigger inventory location change
    // ═══════════════════════════════════════════════════════════════

    public record PutawayTaskCompleted(
            String eventId,
            String taskKey,
            ReceiptKey receiptKey,
            LpnKey lpnKey,
            SkuKey skuKey,
            LocationKey fromLocationKey,
            LocationKey toLocationKey,
            Quantity quantity,
            UserKey completedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) {}

    public record PutawayTaskCancelled(
            String eventId,
            String taskKey,
            LpnKey lpnKey,
            String cancellationReason,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) {}

    // ═══════════════════════════════════════════════════════════════
    // CROSSDOCKING EVENTS - Trigger crossdock inventory
    // ═══════════════════════════════════════════════════════════════

    public record CrossdockReceiptCompleted(
            String eventId,
            ReceiptKey receiptKey,
            OrderKey targetOrderKey,
            SkuKey skuKey,
            Quantity quantity,
            LpnKey lpnKey,
            LocationKey crossdockLocationKey,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) {}
}
