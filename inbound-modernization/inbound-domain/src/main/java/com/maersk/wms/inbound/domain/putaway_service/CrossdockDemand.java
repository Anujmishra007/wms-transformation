package com.maersk.wms.inbound.domain.putaway_service;

import com.maersk.wms.inbound.shared.kernel.identifiers.SkuKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Represents outbound demand that can be satisfied by crossdock.
 * Used for matching incoming inventory to outbound orders.
 */
public class CrossdockDemand {

    private String demandKey;
    private String orderKey;
    private String orderDetailKey;
    private String waveKey;
    private String shipmentKey;
    private StorerKey storerKey;
    private SkuKey skuKey;
    private String lot;
    private BigDecimal requestedQty;
    private BigDecimal allocatedQty;
    private BigDecimal openQty;
    private int priority;
    private LocalDate shipDate;
    private LocalDate deliveryDate;
    private String carrierCode;
    private String routeKey;
    private String customerKey;
    private Instant createdAt;

    public CrossdockDemand() {}

    // Domain methods

    public BigDecimal getOpenQuantity() {
        if (requestedQty == null) return BigDecimal.ZERO;
        if (allocatedQty == null) return requestedQty;
        return requestedQty.subtract(allocatedQty);
    }

    public boolean canSatisfy(BigDecimal availableQty) {
        return availableQty.compareTo(getOpenQuantity()) >= 0;
    }

    public boolean canPartiallySatisfy(BigDecimal availableQty, BigDecimal minPercent) {
        BigDecimal openQty = getOpenQuantity();
        BigDecimal minQty = openQty.multiply(minPercent).divide(BigDecimal.valueOf(100));
        return availableQty.compareTo(minQty) >= 0;
    }

    // Getters and Setters
    public String getDemandKey() { return demandKey; }
    public void setDemandKey(String demandKey) { this.demandKey = demandKey; }

    public String getOrderKey() { return orderKey; }
    public void setOrderKey(String orderKey) { this.orderKey = orderKey; }

    public String getOrderDetailKey() { return orderDetailKey; }
    public void setOrderDetailKey(String orderDetailKey) { this.orderDetailKey = orderDetailKey; }

    public String getWaveKey() { return waveKey; }
    public void setWaveKey(String waveKey) { this.waveKey = waveKey; }

    public String getShipmentKey() { return shipmentKey; }
    public void setShipmentKey(String shipmentKey) { this.shipmentKey = shipmentKey; }

    public StorerKey getStorerKey() { return storerKey; }
    public void setStorerKey(StorerKey storerKey) { this.storerKey = storerKey; }

    public SkuKey getSkuKey() { return skuKey; }
    public void setSkuKey(SkuKey skuKey) { this.skuKey = skuKey; }

    public String getLot() { return lot; }
    public void setLot(String lot) { this.lot = lot; }

    public BigDecimal getRequestedQty() { return requestedQty; }
    public void setRequestedQty(BigDecimal requestedQty) { this.requestedQty = requestedQty; }

    public BigDecimal getAllocatedQty() { return allocatedQty; }
    public void setAllocatedQty(BigDecimal allocatedQty) { this.allocatedQty = allocatedQty; }

    public BigDecimal getOpenQty() { return openQty; }
    public void setOpenQty(BigDecimal openQty) { this.openQty = openQty; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public LocalDate getShipDate() { return shipDate; }
    public void setShipDate(LocalDate shipDate) { this.shipDate = shipDate; }

    public LocalDate getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }

    public String getCarrierCode() { return carrierCode; }
    public void setCarrierCode(String carrierCode) { this.carrierCode = carrierCode; }

    public String getRouteKey() { return routeKey; }
    public void setRouteKey(String routeKey) { this.routeKey = routeKey; }

    public String getCustomerKey() { return customerKey; }
    public void setCustomerKey(String customerKey) { this.customerKey = customerKey; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
