package com.maersk.wms.inventory.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * LOTxLOCxID - Core inventory record representing:
 * Lot × Location × License Plate (ID)
 *
 * This is the central inventory table in FbM WMS.
 * Maps to legacy table: LOTxLOCxID
 *
 * Key characteristics:
 * - Tracks inventory at Lot + Location + LPN granularity
 * - Supports 10 lottable attributes (LOTTABLE01-LOTTABLE10)
 * - Uses TrafficCop for optimistic concurrency
 * - Supports holds and allocations
 */
@Data
@Builder
public class LotxLocxId {

    /** Unique record key */
    private String lotxLocxIdKey;

    /** Storage key (denormalized for performance) */
    private String storageKey;

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotBlank(message = "Lot is required")
    private String lot;

    @NotBlank(message = "Location is required")
    private String location;

    /** License Plate Number (ID) */
    @NotBlank(message = "LPN/ID is required")
    private String id;

    /** On-hand quantity */
    @NotNull
    @PositiveOrZero
    private BigDecimal qty;

    /** Allocated quantity */
    @PositiveOrZero
    private BigDecimal qtyAllocated;

    /** Picked quantity */
    @PositiveOrZero
    private BigDecimal qtyPicked;

    /** Available quantity = qty - qtyAllocated - qtyPicked */
    public BigDecimal getQtyAvailable() {
        BigDecimal allocated = qtyAllocated != null ? qtyAllocated : BigDecimal.ZERO;
        BigDecimal picked = qtyPicked != null ? qtyPicked : BigDecimal.ZERO;
        return qty.subtract(allocated).subtract(picked);
    }

    /** Pack key for case/pack level tracking */
    private String packKey;

    /** Unit of measure */
    private String uom;

    /** Inventory status */
    private InventoryStatus status;

    /** Hold code if on hold */
    private String holdCode;

    // ═══════════════════════════════════════════════════════════════
    // LOTTABLE ATTRIBUTES (Configurable per client)
    // ═══════════════════════════════════════════════════════════════
    /** LOTTABLE01 - Often: Batch/Lot Number */
    private String lottable01;

    /** LOTTABLE02 - Often: Expiry Date */
    private String lottable02;

    /** LOTTABLE03 - Often: Manufacturing Date */
    private String lottable03;

    /** LOTTABLE04 - Often: Country of Origin */
    private String lottable04;

    /** LOTTABLE05 - Often: Vendor Lot */
    private String lottable05;

    /** LOTTABLE06 - Client configurable */
    private String lottable06;

    /** LOTTABLE07 - Client configurable */
    private String lottable07;

    /** LOTTABLE08 - Client configurable */
    private String lottable08;

    /** LOTTABLE09 - Client configurable */
    private String lottable09;

    /** LOTTABLE10 - Client configurable */
    private String lottable10;

    // ═══════════════════════════════════════════════════════════════
    // FIFO / ALLOCATION ATTRIBUTES
    // ═══════════════════════════════════════════════════════════════
    /** Receipt date for FIFO */
    private LocalDateTime receiptDate;

    /** Inbound receipt key */
    private String receiptKey;

    /** Receipt line number */
    private String receiptLineNumber;

    // ═══════════════════════════════════════════════════════════════
    // AUDIT & CONCURRENCY
    // ═══════════════════════════════════════════════════════════════
    private LocalDateTime addDate;
    private String addWho;
    private LocalDateTime editDate;
    private String editWho;

    /** Optimistic concurrency control */
    private int trafficCop;
    private byte[] rowVersion;

    // ═══════════════════════════════════════════════════════════════
    // MULTI-TENANT
    // ═══════════════════════════════════════════════════════════════
    private String countryCode;
    private String clientCode;
    private String warehouseCode;

    /**
     * Check if inventory is available for allocation.
     */
    public boolean isAvailableForAllocation() {
        return status == InventoryStatus.AVAILABLE
               && holdCode == null
               && getQtyAvailable().compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Check if inventory is on hold.
     */
    public boolean isOnHold() {
        return status == InventoryStatus.HOLD || holdCode != null;
    }

    /**
     * Get lottable attributes as map.
     */
    public Map<String, String> getLottableAttributes() {
        return Map.of(
            "LOTTABLE01", lottable01 != null ? lottable01 : "",
            "LOTTABLE02", lottable02 != null ? lottable02 : "",
            "LOTTABLE03", lottable03 != null ? lottable03 : "",
            "LOTTABLE04", lottable04 != null ? lottable04 : "",
            "LOTTABLE05", lottable05 != null ? lottable05 : "",
            "LOTTABLE06", lottable06 != null ? lottable06 : "",
            "LOTTABLE07", lottable07 != null ? lottable07 : "",
            "LOTTABLE08", lottable08 != null ? lottable08 : "",
            "LOTTABLE09", lottable09 != null ? lottable09 : "",
            "LOTTABLE10", lottable10 != null ? lottable10 : ""
        );
    }
}
