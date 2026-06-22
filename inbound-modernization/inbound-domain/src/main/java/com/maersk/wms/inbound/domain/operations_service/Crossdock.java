package com.maersk.wms.inbound.domain.operations_service;

import com.maersk.wms.inbound.shared.kernel.identifiers.LocationKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.LpnKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.ReceiptKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.SkuKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;
import com.maersk.wms.inbound.shared.kernel.valueobjects.Quantity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Crossdock entity for Crossdocking operations.
 * Part of inbound-operations-service subdomain.
 * Represents a crossdock operation that bypasses storage and moves
 * incoming inventory directly to outbound staging or shipping.
 */
public class Crossdock {

    private String crossdockKey;
    private StorerKey storerKey;
    private ReceiptKey receiptKey;
    private String receiptDetailKey;
    private CrossdockType type;
    private CrossdockStatus status;
    private String waveKey;
    private String orderKey;
    private String orderDetailKey;
    private SkuKey skuKey;
    private LpnKey inboundLpn;
    private LpnKey outboundLpn;
    private Quantity allocatedQty;
    private Quantity pickedQty;
    private Quantity shippedQty;
    private LocationKey stagingLocation;
    private LocationKey dockDoor;
    private String shipmentKey;
    private String loadKey;
    private String carrierCode;
    private String routeKey;
    private int priority;
    private Instant createdAt;
    private Instant allocatedAt;
    private Instant pickedAt;
    private Instant shippedAt;
    private String createdBy;
    private String pickedBy;
    private boolean isOpportunistic;
    private boolean isPlanned;
    private String reasonCode;
    private String notes;
    private int trafficCop;
    private List<CrossdockDetail> details = new ArrayList<>();

    public Crossdock() {}

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    // Domain methods

    public void allocate(String orderKey, Quantity qty) {
        if (this.status != CrossdockStatus.PENDING) {
            throw new IllegalStateException("Can only allocate pending crossdocks");
        }
        this.orderKey = orderKey;
        this.allocatedQty = qty;
        this.status = CrossdockStatus.ALLOCATED;
        this.allocatedAt = Instant.now();
    }

    public void release() {
        if (this.status != CrossdockStatus.ALLOCATED) {
            throw new IllegalStateException("Can only release allocated crossdocks");
        }
        this.status = CrossdockStatus.RELEASED;
    }

    public void pick(Quantity qty, String userId) {
        if (this.status != CrossdockStatus.RELEASED) {
            throw new IllegalStateException("Can only pick released crossdocks");
        }
        this.pickedQty = qty;
        this.status = CrossdockStatus.PICKED;
        this.pickedAt = Instant.now();
        this.pickedBy = userId;
    }

    public void stage(LocationKey stagingLoc) {
        if (this.status != CrossdockStatus.PICKED) {
            throw new IllegalStateException("Can only stage picked crossdocks");
        }
        this.stagingLocation = stagingLoc;
        this.status = CrossdockStatus.STAGED;
    }

    public void load(String loadKey, LocationKey dockDoor) {
        if (this.status != CrossdockStatus.STAGED) {
            throw new IllegalStateException("Can only load staged crossdocks");
        }
        this.loadKey = loadKey;
        this.dockDoor = dockDoor;
        this.status = CrossdockStatus.LOADED;
    }

    public void ship(Quantity qty) {
        if (this.status != CrossdockStatus.LOADED) {
            throw new IllegalStateException("Can only ship loaded crossdocks");
        }
        this.shippedQty = qty;
        this.status = CrossdockStatus.SHIPPED;
        this.shippedAt = Instant.now();
    }

    public void cancel(String reason) {
        if (this.status == CrossdockStatus.SHIPPED) {
            throw new IllegalStateException("Cannot cancel shipped crossdocks");
        }
        this.status = CrossdockStatus.CANCELLED;
        this.reasonCode = "CANCELLED";
        this.notes = reason;
    }

    public void addDetail(CrossdockDetail detail) {
        detail.setCrossdockKey(this.crossdockKey);
        this.details.add(detail);
    }

    public boolean isOpportunisticCrossdock() {
        return this.isOpportunistic || this.type == CrossdockType.OPPORTUNISTIC;
    }

    public boolean isPlannedCrossdock() {
        return this.isPlanned || this.type == CrossdockType.PLANNED;
    }

    public boolean isFlowThrough() {
        return this.type == CrossdockType.FLOW_THROUGH;
    }

    public boolean canBePicked() {
        return this.status == CrossdockStatus.RELEASED;
    }

    public boolean isTerminal() {
        return this.status == CrossdockStatus.SHIPPED
            || this.status == CrossdockStatus.CANCELLED;
    }

    public Quantity getRemainingQty() {
        if (allocatedQty == null) return Quantity.zero("EA");
        if (pickedQty == null) return allocatedQty;
        return allocatedQty.subtract(pickedQty);
    }

    // Getters and Setters
    public String getCrossdockKey() { return crossdockKey; }
    public void setCrossdockKey(String crossdockKey) { this.crossdockKey = crossdockKey; }

    public StorerKey getStorerKey() { return storerKey; }
    public void setStorerKey(StorerKey storerKey) { this.storerKey = storerKey; }

    public ReceiptKey getReceiptKey() { return receiptKey; }
    public void setReceiptKey(ReceiptKey receiptKey) { this.receiptKey = receiptKey; }

    public String getReceiptDetailKey() { return receiptDetailKey; }
    public void setReceiptDetailKey(String receiptDetailKey) { this.receiptDetailKey = receiptDetailKey; }

    public CrossdockType getType() { return type; }
    public void setType(CrossdockType type) { this.type = type; }

    public CrossdockStatus getStatus() { return status; }
    public void setStatus(CrossdockStatus status) { this.status = status; }

    public String getWaveKey() { return waveKey; }
    public void setWaveKey(String waveKey) { this.waveKey = waveKey; }

    public String getOrderKey() { return orderKey; }
    public void setOrderKey(String orderKey) { this.orderKey = orderKey; }

    public String getOrderDetailKey() { return orderDetailKey; }
    public void setOrderDetailKey(String orderDetailKey) { this.orderDetailKey = orderDetailKey; }

    public SkuKey getSkuKey() { return skuKey; }
    public void setSkuKey(SkuKey skuKey) { this.skuKey = skuKey; }

    public LpnKey getInboundLpn() { return inboundLpn; }
    public void setInboundLpn(LpnKey inboundLpn) { this.inboundLpn = inboundLpn; }

    public LpnKey getOutboundLpn() { return outboundLpn; }
    public void setOutboundLpn(LpnKey outboundLpn) { this.outboundLpn = outboundLpn; }

    public Quantity getAllocatedQty() { return allocatedQty; }
    public void setAllocatedQty(Quantity allocatedQty) { this.allocatedQty = allocatedQty; }

    public Quantity getPickedQty() { return pickedQty; }
    public void setPickedQty(Quantity pickedQty) { this.pickedQty = pickedQty; }

    public Quantity getShippedQty() { return shippedQty; }
    public void setShippedQty(Quantity shippedQty) { this.shippedQty = shippedQty; }

    public LocationKey getStagingLocation() { return stagingLocation; }
    public void setStagingLocation(LocationKey stagingLocation) { this.stagingLocation = stagingLocation; }

    public LocationKey getDockDoor() { return dockDoor; }
    public void setDockDoor(LocationKey dockDoor) { this.dockDoor = dockDoor; }

    public String getShipmentKey() { return shipmentKey; }
    public void setShipmentKey(String shipmentKey) { this.shipmentKey = shipmentKey; }

    public String getLoadKey() { return loadKey; }
    public void setLoadKey(String loadKey) { this.loadKey = loadKey; }

    public String getCarrierCode() { return carrierCode; }
    public void setCarrierCode(String carrierCode) { this.carrierCode = carrierCode; }

    public String getRouteKey() { return routeKey; }
    public void setRouteKey(String routeKey) { this.routeKey = routeKey; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getAllocatedAt() { return allocatedAt; }
    public void setAllocatedAt(Instant allocatedAt) { this.allocatedAt = allocatedAt; }

    public Instant getPickedAt() { return pickedAt; }
    public void setPickedAt(Instant pickedAt) { this.pickedAt = pickedAt; }

    public Instant getShippedAt() { return shippedAt; }
    public void setShippedAt(Instant shippedAt) { this.shippedAt = shippedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getPickedBy() { return pickedBy; }
    public void setPickedBy(String pickedBy) { this.pickedBy = pickedBy; }

    public boolean isOpportunistic() { return isOpportunistic; }
    public void setOpportunistic(boolean opportunistic) { isOpportunistic = opportunistic; }

    public boolean isPlanned() { return isPlanned; }
    public void setPlanned(boolean planned) { isPlanned = planned; }

    public String getReasonCode() { return reasonCode; }
    public void setReasonCode(String reasonCode) { this.reasonCode = reasonCode; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public int getTrafficCop() { return trafficCop; }
    public void setTrafficCop(int trafficCop) { this.trafficCop = trafficCop; }

    public List<CrossdockDetail> getDetails() { return Collections.unmodifiableList(details); }
    public void setDetails(List<CrossdockDetail> details) { this.details = new ArrayList<>(details); }

    public static class Builder {
        private final Crossdock crossdock = new Crossdock();

        public Builder crossdockKey(String key) { crossdock.crossdockKey = key; return this; }
        public Builder storerKey(StorerKey key) { crossdock.storerKey = key; return this; }
        public Builder receiptKey(ReceiptKey key) { crossdock.receiptKey = key; return this; }
        public Builder type(CrossdockType type) { crossdock.type = type; return this; }
        public Builder skuKey(SkuKey key) { crossdock.skuKey = key; return this; }
        public Builder inboundLpn(LpnKey lpn) { crossdock.inboundLpn = lpn; return this; }
        public Builder allocatedQty(Quantity qty) { crossdock.allocatedQty = qty; return this; }
        public Builder orderKey(String key) { crossdock.orderKey = key; return this; }
        public Builder waveKey(String key) { crossdock.waveKey = key; return this; }
        public Builder priority(int priority) { crossdock.priority = priority; return this; }
        public Builder opportunistic(boolean val) { crossdock.isOpportunistic = val; return this; }
        public Builder planned(boolean val) { crossdock.isPlanned = val; return this; }

        public Crossdock build() {
            crossdock.status = CrossdockStatus.PENDING;
            crossdock.createdAt = Instant.now();
            return crossdock;
        }
    }
}
