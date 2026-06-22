package com.maersk.wms.outbound.domain.order_service.model;

import com.maersk.wms.outbound.shared.kernel.identifiers.OrderKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.StorerKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.WaveKey;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order aggregate root - represents a customer order.
 * Part of Order Service bounded context.
 */
@Data
@Builder
public class Order {

    private OrderKey orderKey;
    private String externalOrderKey;
    private StorerKey storerKey;
    private WaveKey waveKey;

    // Order attributes
    private OrderType orderType;
    private OrderStatus status;
    private OrderPriority priority;

    // Customer info
    private String consigneeKey;
    private String shipToName;
    private String shipToAddress1;
    private String shipToAddress2;
    private String shipToCity;
    private String shipToState;
    private String shipToZip;
    private String shipToCountry;

    // Shipping info
    private String carrierCode;
    private String serviceType;
    private LocalDate requestedShipDate;
    private LocalDate deliveryDate;

    // Quantities
    private BigDecimal totalQtyOrdered;
    private BigDecimal totalQtyAllocated;
    private BigDecimal totalQtyPicked;
    private BigDecimal totalQtyShipped;

    // Order details
    @Builder.Default
    private List<OrderDetail> details = new ArrayList<>();

    // Audit
    private String addWho;
    private LocalDateTime addDate;
    private String editWho;
    private LocalDateTime editDate;

    // Business methods
    public boolean isFullyAllocated() {
        return totalQtyOrdered.compareTo(totalQtyAllocated) == 0;
    }

    public boolean isPartiallyAllocated() {
        return totalQtyAllocated.compareTo(BigDecimal.ZERO) > 0
            && totalQtyAllocated.compareTo(totalQtyOrdered) < 0;
    }

    public boolean isFullyPicked() {
        return totalQtyOrdered.compareTo(totalQtyPicked) == 0;
    }

    public boolean canAllocate() {
        return status == OrderStatus.RELEASED || status == OrderStatus.PARTIALLY_ALLOCATED;
    }

    public boolean canRelease() {
        return status == OrderStatus.OPEN || status == OrderStatus.HOLD;
    }

    public void addDetail(OrderDetail detail) {
        details.add(detail);
        recalculateTotals();
    }

    public void recalculateTotals() {
        this.totalQtyOrdered = details.stream()
                .map(OrderDetail::getQtyOrdered)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalQtyAllocated = details.stream()
                .map(OrderDetail::getQtyAllocated)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalQtyPicked = details.stream()
                .map(OrderDetail::getQtyPicked)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
