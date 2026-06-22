package com.maersk.wms.printing.api.dto;

import com.maersk.wms.printing.domain.print_job.model.PrintJob;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Response DTO for print job operations.
 */
@Data
@Builder
public class PrintJobResponse {

    private String jobId;
    private String status;
    private String labelType;
    private String printerName;
    private int copiesPrinted;
    private Instant submittedAt;
    private Instant completedAt;
    private String errorMessage;

    /**
     * Factory method to create response from domain entity.
     */
    public static PrintJobResponse from(PrintJob job) {
        return PrintJobResponse.builder()
                .jobId(job.getPrintJobKey().value())
                .status(job.getStatus().name())
                .labelType(null)  // Set from context
                .printerName(job.getPrinterKey() != null ? job.getPrinterKey().value() : null)
                .copiesPrinted(job.getPrintedItems())
                .submittedAt(job.getCreatedAt())
                .completedAt(job.getCompletedAt())
                .errorMessage(job.getLastError())
                .build();
    }
}
