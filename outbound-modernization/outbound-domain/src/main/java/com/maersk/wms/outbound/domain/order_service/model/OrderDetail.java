package com.maersk.wms.outbound.domain.order_service.model;

import com.maersk.wms.outbound.shared.kernel.identifiers.OrderKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.SkuKey;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Order detail line item.
 * Part of Order Service bounded context.
 */
@Data
@Builder
public class OrderDetail {

    private OrderKey orderKey;
    private int lineNumber;
    private SkuKey sku;

    // Quantities
    private BigDecimal qtyOrdered;
    private BigDecimal qtyAllocated;
    private BigDecimal qtyPicked;
    private BigDecimal qtyShipped;

    // Status
    private OrderDetailStatus status;

    // Pack info
    private String packKey;
    private String uom;

    // Lot requirements
    private String lottable01;  // Client-specific lottable
    private String lottable02;
    private String lottable03;
    private String lottable04;
    private String lottable05;

    // Audit
    private String addWho;
    private LocalDateTime addDate;
    private String editWho;
    private LocalDateTime editDate;

    // Business methods
    public BigDecimal getOpenQty() {
        return qtyOrdered.subtract(qtyAllocated);
    }

    public boolean isFullyAllocated() {
        return qtyOrdered.compareTo(qtyAllocated) == 0;
    }

    public boolean isFullyPicked() {
        return qtyOrdered.compareTo(qtyPicked) == 0;
    }

    public boolean hasShortage() {
        return qtyPicked.compareTo(qtyAllocated) < 0 && status == OrderDetailStatus.PICKED;
    }
}
