package com.maersk.wms.masterdata.domain.operational_masters.model;

import com.maersk.wms.masterdata.shared.kernel.identifiers.*;
import com.maersk.wms.masterdata.shared.kernel.valueobjects.*;

import lombok.*;
import java.time.Instant;
import java.util.Map;

/**
 * Storer aggregate root.
 * Represents a customer/inventory owner in the warehouse.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Storer {

    private StorerKey storerKey;
    private String storerCode;
    private String storerName;
    private String companyName;

    // Type
    private StorerType storerType;

    // Contact Information
    private Address billingAddress;
    private Address shippingAddress;
    private ContactInfo primaryContact;
    private ContactInfo billingContact;

    // Business Configuration
    private String customerGroup;
    private String serviceLevel;
    private String pricingTier;
    private String currency;
    private String taxId;

    // Warehouse Configuration
    private String defaultWarehouse;
    private String defaultZone;
    private String putawayStrategy;
    private String allocationStrategy;
    private String rotationRule; // FIFO, FEFO, LIFO

    // Receiving Configuration
    private boolean requiresAsn;
    private boolean autoReceiveAllowed;
    private int receiveVariancePct;

    // Shipping Configuration
    private String defaultCarrier;
    private String defaultServiceLevel;
    private boolean allowPartialShipments;
    private boolean requiresPackSlip;

    // Custom Configuration
    private Map<String, String> customAttributes;

    // Status
    private StorerStatus status;

    // Audit
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;

    public enum StorerType {
        OWNER, CUSTOMER, CONSIGNEE, VENDOR, THIRD_PARTY
    }

    public enum StorerStatus {
        ACTIVE, INACTIVE, SUSPENDED, PENDING_APPROVAL
    }

    // Business Methods
    public void activate() {
        this.status = StorerStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.status = StorerStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    public void suspend(String reason) {
        this.status = StorerStatus.SUSPENDED;
        this.updatedAt = Instant.now();
    }

    public boolean isActive() {
        return status == StorerStatus.ACTIVE;
    }

    public boolean canReceive() {
        return isActive();
    }

    public boolean canShip() {
        return isActive();
    }
}
