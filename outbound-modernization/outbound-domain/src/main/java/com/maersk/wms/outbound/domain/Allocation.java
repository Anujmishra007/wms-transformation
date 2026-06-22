package com.maersk.wms.outbound.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Allocation entity representing inventory allocated to an order.
 * Maps to PICKDETAIL table in the legacy system.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Allocation {

    private String pickDetailKey;
    private String orderKey;
    private String orderLineNumber;
    private String waveKey;

    private String sku;
    private String skuDescription;
    private String lot;
    private String id;  // LPN/License Plate
    private String location;

    private BigDecimal qtyAllocated;
    private BigDecimal qtyPicked;
    private String uom;
    private String packKey;

    private AllocationStatus status;
    private AllocationStrategy strategy;

    private String assignedUser;
    private String pickZone;
    private int pickSequence;
    private String cartonId;
    private String cartonGroup;

    private LocalDateTime expirationDate;
    private LocalDateTime allocationDate;
    private LocalDateTime pickDate;

    // Source lottable attributes (from LOTxLOCxID)
    private String lottable01;
    private String lottable02;
    private String lottable03;
    private String lottable04;
    private String lottable05;

    private String addWho;
    private LocalDateTime addDate;
    private String editWho;
    private LocalDateTime editDate;

    /**
     * Check if allocation can be picked.
     */
    public boolean canPick() {
        return status == AllocationStatus.ALLOCATED || status == AllocationStatus.RELEASED;
    }

    /**
     * Check if fully picked.
     */
    public boolean isFullyPicked() {
        return qtyAllocated != null && qtyPicked != null &&
               qtyPicked.compareTo(qtyAllocated) >= 0;
    }

    /**
     * Get remaining quantity to pick.
     */
    public BigDecimal getRemainingToPick() {
        BigDecimal allocated = qtyAllocated != null ? qtyAllocated : BigDecimal.ZERO;
        BigDecimal picked = qtyPicked != null ? qtyPicked : BigDecimal.ZERO;
        return allocated.subtract(picked).max(BigDecimal.ZERO);
    }
}
