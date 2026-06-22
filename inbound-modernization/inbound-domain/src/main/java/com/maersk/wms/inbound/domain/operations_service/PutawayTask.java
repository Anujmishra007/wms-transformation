package com.maersk.wms.inbound.domain.operations_service;

import com.maersk.wms.inbound.shared.kernel.identifiers.LocationKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.LpnKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.ReceiptKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.SkuKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;
import com.maersk.wms.inbound.shared.kernel.valueobjects.Quantity;

import java.time.Instant;

/**
 * Putaway Task entity for Putaway to Location operations.
 * Part of inbound-operations-service subdomain.
 * Represents the task of moving received inventory to a storage location.
 */
public class PutawayTask {

    private String putawayKey;
    private StorerKey storerKey;
    private ReceiptKey receiptKey;
    private String receiptDetailKey;
    private LpnKey sourceLpn;
    private LpnKey targetLpn;
    private SkuKey skuKey;
    private Quantity quantity;
    private LocationKey fromLocation;
    private LocationKey toLocation;
    private LocationKey suggestedLocation;
    private PutawayTaskStatus status;
    private PutawayTaskType taskType;
    private String putawayZone;
    private String putawayStrategy;
    private int priority;
    private String assignedUser;
    private String assignedEquipment;
    private Instant createdAt;
    private Instant assignedAt;
    private Instant startedAt;
    private Instant completedAt;
    private String createdBy;
    private String completedBy;
    private boolean isCrossdock;
    private String crossdockKey;
    private String waveKey;
    private String orderKey;
    private int sequence;
    private String reasonCode;
    private String notes;
    private int trafficCop;

    public PutawayTask() {}

    // Builder pattern for construction
    public static Builder builder() {
        return new Builder();
    }

    // Domain methods

    public void assign(String userId, String equipment) {
        if (this.status != PutawayTaskStatus.PENDING) {
            throw new IllegalStateException("Can only assign pending tasks");
        }
        this.assignedUser = userId;
        this.assignedEquipment = equipment;
        this.status = PutawayTaskStatus.ASSIGNED;
        this.assignedAt = Instant.now();
    }

    public void start() {
        if (this.status != PutawayTaskStatus.ASSIGNED) {
            throw new IllegalStateException("Can only start assigned tasks");
        }
        this.status = PutawayTaskStatus.IN_PROGRESS;
        this.startedAt = Instant.now();
    }

    public void complete(LocationKey actualLocation, String userId) {
        if (this.status != PutawayTaskStatus.IN_PROGRESS) {
            throw new IllegalStateException("Can only complete in-progress tasks");
        }
        this.toLocation = actualLocation;
        this.status = PutawayTaskStatus.COMPLETED;
        this.completedAt = Instant.now();
        this.completedBy = userId;
    }

    public void confirmSuggestedLocation() {
        if (this.suggestedLocation == null) {
            throw new IllegalStateException("No suggested location to confirm");
        }
        this.toLocation = this.suggestedLocation;
    }

    public void overrideLocation(LocationKey newLocation, String reason) {
        this.toLocation = newLocation;
        this.reasonCode = "OVERRIDE";
        this.notes = reason;
    }

    public void cancel(String reason) {
        if (this.status == PutawayTaskStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed tasks");
        }
        this.status = PutawayTaskStatus.CANCELLED;
        this.reasonCode = "CANCELLED";
        this.notes = reason;
    }

    public void shortPutaway(Quantity actualQty, String reason) {
        if (actualQty.isGreaterThan(this.quantity)) {
            throw new IllegalArgumentException("Short quantity cannot exceed original");
        }
        this.quantity = actualQty;
        this.status = PutawayTaskStatus.SHORT;
        this.reasonCode = "SHORT";
        this.notes = reason;
    }

    public boolean isCrossdockTask() {
        return this.isCrossdock || this.taskType == PutawayTaskType.CROSSDOCK;
    }

    public boolean isDirectPutaway() {
        return this.taskType == PutawayTaskType.DIRECT;
    }

    public boolean canBeStarted() {
        return this.status == PutawayTaskStatus.ASSIGNED;
    }

    public boolean isTerminal() {
        return this.status == PutawayTaskStatus.COMPLETED
            || this.status == PutawayTaskStatus.CANCELLED;
    }

    // Getters and Setters
    public String getPutawayKey() { return putawayKey; }
    public void setPutawayKey(String putawayKey) { this.putawayKey = putawayKey; }

    public StorerKey getStorerKey() { return storerKey; }
    public void setStorerKey(StorerKey storerKey) { this.storerKey = storerKey; }

    public ReceiptKey getReceiptKey() { return receiptKey; }
    public void setReceiptKey(ReceiptKey receiptKey) { this.receiptKey = receiptKey; }

    public String getReceiptDetailKey() { return receiptDetailKey; }
    public void setReceiptDetailKey(String receiptDetailKey) { this.receiptDetailKey = receiptDetailKey; }

    public LpnKey getSourceLpn() { return sourceLpn; }
    public void setSourceLpn(LpnKey sourceLpn) { this.sourceLpn = sourceLpn; }

    public LpnKey getTargetLpn() { return targetLpn; }
    public void setTargetLpn(LpnKey targetLpn) { this.targetLpn = targetLpn; }

    public SkuKey getSkuKey() { return skuKey; }
    public void setSkuKey(SkuKey skuKey) { this.skuKey = skuKey; }

    public Quantity getQuantity() { return quantity; }
    public void setQuantity(Quantity quantity) { this.quantity = quantity; }

    public LocationKey getFromLocation() { return fromLocation; }
    public void setFromLocation(LocationKey fromLocation) { this.fromLocation = fromLocation; }

    public LocationKey getToLocation() { return toLocation; }
    public void setToLocation(LocationKey toLocation) { this.toLocation = toLocation; }

    public LocationKey getSuggestedLocation() { return suggestedLocation; }
    public void setSuggestedLocation(LocationKey suggestedLocation) { this.suggestedLocation = suggestedLocation; }

    public PutawayTaskStatus getStatus() { return status; }
    public void setStatus(PutawayTaskStatus status) { this.status = status; }

    public PutawayTaskType getTaskType() { return taskType; }
    public void setTaskType(PutawayTaskType taskType) { this.taskType = taskType; }

    public String getPutawayZone() { return putawayZone; }
    public void setPutawayZone(String putawayZone) { this.putawayZone = putawayZone; }

    public String getPutawayStrategy() { return putawayStrategy; }
    public void setPutawayStrategy(String putawayStrategy) { this.putawayStrategy = putawayStrategy; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public String getAssignedUser() { return assignedUser; }
    public void setAssignedUser(String assignedUser) { this.assignedUser = assignedUser; }

    public String getAssignedEquipment() { return assignedEquipment; }
    public void setAssignedEquipment(String assignedEquipment) { this.assignedEquipment = assignedEquipment; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Instant assignedAt) { this.assignedAt = assignedAt; }

    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getCompletedBy() { return completedBy; }
    public void setCompletedBy(String completedBy) { this.completedBy = completedBy; }

    public boolean isCrossdock() { return isCrossdock; }
    public void setCrossdock(boolean crossdock) { isCrossdock = crossdock; }

    public String getCrossdockKey() { return crossdockKey; }
    public void setCrossdockKey(String crossdockKey) { this.crossdockKey = crossdockKey; }

    public String getWaveKey() { return waveKey; }
    public void setWaveKey(String waveKey) { this.waveKey = waveKey; }

    public String getOrderKey() { return orderKey; }
    public void setOrderKey(String orderKey) { this.orderKey = orderKey; }

    public int getSequence() { return sequence; }
    public void setSequence(int sequence) { this.sequence = sequence; }

    public String getReasonCode() { return reasonCode; }
    public void setReasonCode(String reasonCode) { this.reasonCode = reasonCode; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public int getTrafficCop() { return trafficCop; }
    public void setTrafficCop(int trafficCop) { this.trafficCop = trafficCop; }

    public static class Builder {
        private final PutawayTask task = new PutawayTask();

        public Builder putawayKey(String key) { task.putawayKey = key; return this; }
        public Builder storerKey(StorerKey key) { task.storerKey = key; return this; }
        public Builder receiptKey(ReceiptKey key) { task.receiptKey = key; return this; }
        public Builder sourceLpn(LpnKey lpn) { task.sourceLpn = lpn; return this; }
        public Builder skuKey(SkuKey key) { task.skuKey = key; return this; }
        public Builder quantity(Quantity qty) { task.quantity = qty; return this; }
        public Builder fromLocation(LocationKey loc) { task.fromLocation = loc; return this; }
        public Builder suggestedLocation(LocationKey loc) { task.suggestedLocation = loc; return this; }
        public Builder taskType(PutawayTaskType type) { task.taskType = type; return this; }
        public Builder putawayStrategy(String strategy) { task.putawayStrategy = strategy; return this; }
        public Builder priority(int priority) { task.priority = priority; return this; }
        public Builder crossdock(boolean isCrossdock) { task.isCrossdock = isCrossdock; return this; }

        public PutawayTask build() {
            task.status = PutawayTaskStatus.PENDING;
            task.createdAt = Instant.now();
            return task;
        }
    }
}
