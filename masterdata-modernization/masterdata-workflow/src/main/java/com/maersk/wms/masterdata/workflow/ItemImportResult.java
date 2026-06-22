package com.maersk.wms.masterdata.workflow;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Result of item import workflow.
 */
@Data
@Builder
public class ItemImportResult {

    private boolean success;
    private int totalRecords;
    private int successCount;
    private int failureCount;
    private int skippedCount;
    private List<ImportError> errors;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long durationMs;

    @Data
    @Builder
    public static class ImportError {
        private int rowNumber;
        private String sku;
        private String errorMessage;
        private String errorCode;
    }
}
