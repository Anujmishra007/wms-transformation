package com.maersk.wms.outbound.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Order detail entity representing a line item on an order.
 * Maps to ORDERDETAIL table in the legacy system.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {

    private String orderKey;
    private String orderLineNumber;
    private String sku;
    private String skuDescription;
    private String packKey;
    private String uom;

    private BigDecimal originalQty;
    private BigDecimal qtyOrdered;
    private BigDecimal qtyAllocated;
    private BigDecimal qtyPicked;
    private BigDecimal qtyShipped;
    private BigDecimal qtyCancelled;

    private OrderDetailStatus status;

    private BigDecimal unitPrice;
    private BigDecimal extendedPrice;
    private String currency;

    private String lot;
    private String preferredLot;
    private LocalDateTime expirationDateRequired;

    // Lottable requirements
    private String lottable01;
    private String lottable02;
    private String lottable03;
    private String lottable04;
    private String lottable05;
    private LocalDateTime lottable06;
    private LocalDateTime lottable07;
    private LocalDateTime lottable08;
    private String lottable09;
    private String lottable10;

    private String notes;
    private String addWho;
    private LocalDateTime addDate;
    private String editWho;
    private LocalDateTime editDate;

    /**
     * Check if line is fully allocated.
     */
    public boolean isFullyAllocated() {
        return qtyOrdered != null && qtyAllocated != null &&
               qtyAllocated.compareTo(qtyOrdered) >= 0;
    }

    /**
     * Check if line is fully picked.
     */
    public boolean isFullyPicked() {
        return qtyOrdered != null && qtyPicked != null &&
               qtyPicked.compareTo(qtyOrdered) >= 0;
    }

    /**
     * Get remaining quantity to allocate.
     */
    public BigDecimal getRemainingToAllocate() {
        BigDecimal ordered = qtyOrdered != null ? qtyOrdered : BigDecimal.ZERO;
        BigDecimal allocated = qtyAllocated != null ? qtyAllocated : BigDecimal.ZERO;
        BigDecimal cancelled = qtyCancelled != null ? qtyCancelled : BigDecimal.ZERO;
        return ordered.subtract(allocated).subtract(cancelled).max(BigDecimal.ZERO);
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
