package com.maersk.wms.inbound.domain.operations_service;

import com.maersk.wms.inbound.shared.kernel.identifiers.SkuKey;
import com.maersk.wms.inbound.shared.kernel.valueobjects.LotAttributes;
import com.maersk.wms.inbound.shared.kernel.valueobjects.Quantity;

import java.time.Instant;

/**
 * Crossdock Detail entity for line-level crossdock tracking.
 * Part of inbound-operations-service subdomain.
 */
public class CrossdockDetail {

    private String crossdockDetailKey;
    private String crossdockKey;
    private int lineNumber;
    private SkuKey skuKey;
    private Quantity allocatedQty;
    private Quantity pickedQty;
    private Quantity shippedQty;
    private LotAttributes lotAttributes;
    private String lot;
    private String serialNumber;
    private String orderKey;
    private String orderDetailKey;
    private CrossdockDetailStatus status;
    private String reasonCode;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;
    private int trafficCop;

    public CrossdockDetail() {}

    // Domain methods

    public void allocate(Quantity qty) {
        this.allocatedQty = qty;
        this.status = CrossdockDetailStatus.ALLOCATED;
    }

    public void pick(Quantity qty) {
        if (qty.isGreaterThan(this.allocatedQty)) {
            throw new IllegalArgumentException("Picked qty cannot exceed allocated");
        }
        this.pickedQty = qty;
        this.status = CrossdockDetailStatus.PICKED;
    }

    public void ship(Quantity qty) {
        this.shippedQty = qty;
        this.status = CrossdockDetailStatus.SHIPPED;
    }

    public void shortPick(Quantity actualQty, String reason) {
        this.pickedQty = actualQty;
        this.status = CrossdockDetailStatus.SHORT;
        this.reasonCode = "SHORT";
        this.notes = reason;
    }

    public Quantity getRemainingQty() {
        if (allocatedQty == null) return Quantity.ZERO;
        if (pickedQty == null) return allocatedQty;
        return allocatedQty.subtract(pickedQty);
    }

    public boolean isComplete() {
        return this.status == CrossdockDetailStatus.SHIPPED;
    }

    // Getters and Setters
    public String getCrossdockDetailKey() { return crossdockDetailKey; }
    public void setCrossdockDetailKey(String crossdockDetailKey) { this.crossdockDetailKey = crossdockDetailKey; }

    public String getCrossdockKey() { return crossdockKey; }
    public void setCrossdockKey(String crossdockKey) { this.crossdockKey = crossdockKey; }

    public int getLineNumber() { return lineNumber; }
    public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }

    public SkuKey getSkuKey() { return skuKey; }
    public void setSkuKey(SkuKey skuKey) { this.skuKey = skuKey; }

    public Quantity getAllocatedQty() { return allocatedQty; }
    public void setAllocatedQty(Quantity allocatedQty) { this.allocatedQty = allocatedQty; }

    public Quantity getPickedQty() { return pickedQty; }
    public void setPickedQty(Quantity pickedQty) { this.pickedQty = pickedQty; }

    public Quantity getShippedQty() { return shippedQty; }
    public void setShippedQty(Quantity shippedQty) { this.shippedQty = shippedQty; }

    public LotAttributes getLotAttributes() { return lotAttributes; }
    public void setLotAttributes(LotAttributes lotAttributes) { this.lotAttributes = lotAttributes; }

    public String getLot() { return lot; }
    public void setLot(String lot) { this.lot = lot; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getOrderKey() { return orderKey; }
    public void setOrderKey(String orderKey) { this.orderKey = orderKey; }

    public String getOrderDetailKey() { return orderDetailKey; }
    public void setOrderDetailKey(String orderDetailKey) { this.orderDetailKey = orderDetailKey; }

    public CrossdockDetailStatus getStatus() { return status; }
    public void setStatus(CrossdockDetailStatus status) { this.status = status; }

    public String getReasonCode() { return reasonCode; }
    public void setReasonCode(String reasonCode) { this.reasonCode = reasonCode; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public int getTrafficCop() { return trafficCop; }
    public void setTrafficCop(int trafficCop) { this.trafficCop = trafficCop; }
}
