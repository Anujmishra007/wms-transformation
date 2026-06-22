package com.maersk.wms.printing.domain.label_generation.model;

import com.maersk.wms.printing.shared.kernel.identifiers.*;
import com.maersk.wms.printing.shared.kernel.valueobjects.*;

import lombok.*;
import java.time.Instant;

/**
 * Label aggregate root.
 * Represents a generated label ready for printing.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Label {

    private LabelKey labelKey;
    private TemplateKey templateKey;
    private StorerKey storerKey;
    private WarehouseKey warehouseKey;

    // Label Type
    private LabelType labelType;
    private String labelSubType;

    // Source Reference
    private String sourceType; // ASN, ORDER, LPN, LOCATION, SHIPMENT
    private String sourceKey;
    private String sourceLineKey;

    // Label Content
    private LabelData labelData;
    private String renderedContent; // ZPL, EPL, or other format
    private String contentFormat; // ZPL, EPL, PDF

    // Dimensions
    private LabelDimensions dimensions;

    // Print Info
    private int copies;
    private int printedCount;
    private PrinterKey lastPrintedOn;
    private Instant lastPrintedAt;
    private UserKey lastPrintedBy;

    // Status
    private LabelStatus status;

    // Validity
    private Instant validFrom;
    private Instant validUntil;

    // Audit
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;

    public enum LabelType {
        SHIPPING,       // Shipping labels (carrier labels)
        LPN,            // License plate / pallet labels
        LOCATION,       // Location labels
        CONTENT,        // Content labels (what's inside)
        RECEIVING,      // Receiving labels
        PUTAWAY,        // Putaway labels
        PICK,           // Pick labels
        PACK,           // Packing labels
        PRODUCT,        // Product/SKU labels
        COMPLIANCE,     // Compliance labels (HAZMAT, etc.)
        DOCUMENT        // Document printing (BOL, pack slip)
    }

    public enum LabelStatus {
        GENERATED,      // Label generated, not yet printed
        QUEUED,         // In print queue
        PRINTING,       // Currently printing
        PRINTED,        // Successfully printed
        FAILED,         // Print failed
        EXPIRED,        // Label expired
        VOIDED          // Label voided/cancelled
    }

    // Business Methods
    public void markAsQueued() {
        this.status = LabelStatus.QUEUED;
        this.updatedAt = Instant.now();
    }

    public void markAsPrinting() {
        this.status = LabelStatus.PRINTING;
        this.updatedAt = Instant.now();
    }

    public void markAsPrinted(PrinterKey printerKey, UserKey userKey) {
        this.status = LabelStatus.PRINTED;
        this.printedCount++;
        this.lastPrintedOn = printerKey;
        this.lastPrintedBy = userKey;
        this.lastPrintedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void markAsFailed(String reason) {
        this.status = LabelStatus.FAILED;
        this.updatedAt = Instant.now();
    }

    public void void_() {
        this.status = LabelStatus.VOIDED;
        this.updatedAt = Instant.now();
    }

    public void expire() {
        this.status = LabelStatus.EXPIRED;
        this.updatedAt = Instant.now();
    }

    public boolean canPrint() {
        return status == LabelStatus.GENERATED || status == LabelStatus.FAILED;
    }

    public boolean isValid() {
        Instant now = Instant.now();
        if (validFrom != null && now.isBefore(validFrom)) {
            return false;
        }
        if (validUntil != null && now.isAfter(validUntil)) {
            return false;
        }
        return status != LabelStatus.VOIDED && status != LabelStatus.EXPIRED;
    }

    public boolean hasBeenPrinted() {
        return printedCount > 0;
    }
}
