package com.maersk.wms.printing.domain.print_job.model;

import com.maersk.wms.printing.shared.kernel.identifiers.*;

import lombok.*;
import java.time.Instant;

/**
 * Print Job Item entity.
 * Represents a single label within a print job.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrintJobItem {

    private String itemKey;
    private PrintJobKey printJobKey;
    private LabelKey labelKey;

    // Sequence
    private int sequenceNumber;
    private int copies;

    // Status
    private PrintItemStatus status;
    private String errorMessage;

    // Timing
    private Instant printedAt;

    public enum PrintItemStatus {
        PENDING, PRINTING, PRINTED, FAILED, SKIPPED
    }

    // Business Methods
    public void markPrinting() {
        this.status = PrintItemStatus.PRINTING;
    }

    public void markPrinted() {
        this.status = PrintItemStatus.PRINTED;
        this.printedAt = Instant.now();
    }

    public void markFailed(String error) {
        this.status = PrintItemStatus.FAILED;
        this.errorMessage = error;
    }

    public void skip(String reason) {
        this.status = PrintItemStatus.SKIPPED;
        this.errorMessage = reason;
    }

    public boolean isPending() {
        return status == PrintItemStatus.PENDING;
    }

    public boolean isPrinted() {
        return status == PrintItemStatus.PRINTED;
    }

    public boolean isFailed() {
        return status == PrintItemStatus.FAILED;
    }
}
