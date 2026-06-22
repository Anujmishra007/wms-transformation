package com.maersk.wms.outbound.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order entity representing a customer order for fulfillment.
 * Maps to ORDERS table in the legacy system.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private String orderKey;
    private String externalOrderKey;
    private String storerKey;
    private String orderType;
    private String orderGroup;

    private OrderStatus status;
    private OrderPriority priority;

    private String consigneeKey;
    private String consigneeName;
    private String shipToName;
    private String shipToAddress1;
    private String shipToAddress2;
    private String shipToCity;
    private String shipToState;
    private String shipToZip;
    private String shipToCountry;

    private String carrierCode;
    private String carrierName;
    private String serviceLevel;
    private String deliveryType;

    private LocalDateTime orderDate;
    private LocalDateTime requiredDeliveryDate;
    private LocalDateTime promisedDeliveryDate;
    private LocalDateTime shipByDate;
    private LocalDateTime actualShipDate;

    private BigDecimal totalQtyOrdered;
    private BigDecimal totalQtyAllocated;
    private BigDecimal totalQtyPicked;
    private BigDecimal totalQtyShipped;
    private BigDecimal totalWeight;
    private BigDecimal totalVolume;
    private BigDecimal totalValue;
    private String currency;
    private int totalCartons;
    private int totalPallets;

    private String waveKey;
    private String loadKey;
    private String routeKey;
    private String door;

    private String notes;
    private String specialInstructions;

    private String addWho;
    private LocalDateTime addDate;
    private String editWho;
    private LocalDateTime editDate;

    @Builder.Default
    private List<OrderDetail> details = new ArrayList<>();

    /**
     * Check if order can be allocated.
     */
    public boolean canAllocate() {
        return status == OrderStatus.NEW || status == OrderStatus.OPEN;
    }

    /**
     * Check if order is fully allocated.
     */
    public boolean isFullyAllocated() {
        return totalQtyOrdered != null && totalQtyAllocated != null &&
               totalQtyAllocated.compareTo(totalQtyOrdered) >= 0;
    }

    /**
     * Check if order is fully picked.
     */
    public boolean isFullyPicked() {
        return totalQtyOrdered != null && totalQtyPicked != null &&
               totalQtyPicked.compareTo(totalQtyOrdered) >= 0;
    }

    /**
     * Check if order is fully shipped.
     */
    public boolean isFullyShipped() {
        return totalQtyOrdered != null && totalQtyShipped != null &&
               totalQtyShipped.compareTo(totalQtyOrdered) >= 0;
    }

    /**
     * Get remaining quantity to allocate.
     */
    public BigDecimal getRemainingToAllocate() {
        BigDecimal ordered = totalQtyOrdered != null ? totalQtyOrdered : BigDecimal.ZERO;
        BigDecimal allocated = totalQtyAllocated != null ? totalQtyAllocated : BigDecimal.ZERO;
        return ordered.subtract(allocated).max(BigDecimal.ZERO);
    }
}
