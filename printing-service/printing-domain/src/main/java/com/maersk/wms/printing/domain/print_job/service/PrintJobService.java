package com.maersk.wms.printing.domain.print_job.service;

import com.maersk.wms.printing.domain.print_job.model.PrintJob;
import com.maersk.wms.printing.domain.print_job.model.PrintJobItem;
import com.maersk.wms.printing.shared.kernel.identifiers.*;
import com.maersk.wms.printing.shared.kernel.valueobjects.PrintSettings;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for print job management operations.
 * Handles job creation, queuing, execution, and monitoring.
 */
public interface PrintJobService {

    // Job Creation
    PrintJob createJob(String jobName, PrinterKey printerKey, WarehouseKey warehouseKey,
                       UserKey requestedBy, DeviceKey requestedFrom);

    PrintJob createJobWithLabels(String jobName, List<LabelKey> labelKeys, PrinterKey printerKey,
                                  PrintSettings settings, WarehouseKey warehouseKey,
                                  UserKey requestedBy, DeviceKey requestedFrom);

    PrintJob createScheduledJob(String jobName, List<LabelKey> labelKeys, PrinterKey printerKey,
                                 PrintSettings settings, java.time.Instant scheduledAt,
                                 WarehouseKey warehouseKey, UserKey requestedBy);

    // Job Items
    void addItem(PrintJobKey jobKey, LabelKey labelKey, int copies);

    void addItems(PrintJobKey jobKey, List<LabelKey> labelKeys);

    void removeItem(PrintJobKey jobKey, LabelKey labelKey);

    void updateItemCopies(PrintJobKey jobKey, LabelKey labelKey, int copies);

    // Job Queuing
    void queueJob(PrintJobKey jobKey, PrintQueueKey queueKey);

    void queueJobWithPriority(PrintJobKey jobKey, PrintQueueKey queueKey, int priority);

    void requeueJob(PrintJobKey jobKey);

    void moveToQueue(PrintJobKey jobKey, PrintQueueKey targetQueueKey);

    // Job Execution
    void startJob(PrintJobKey jobKey);

    void completeJob(PrintJobKey jobKey);

    void failJob(PrintJobKey jobKey, String error);

    void recordItemPrinted(PrintJobKey jobKey, LabelKey labelKey);

    void recordItemFailed(PrintJobKey jobKey, LabelKey labelKey, String error);

    // Job Control
    void pauseJob(PrintJobKey jobKey);

    void resumeJob(PrintJobKey jobKey);

    void cancelJob(PrintJobKey jobKey, String reason, UserKey cancelledBy);

    void retryJob(PrintJobKey jobKey);

    void retryFailedItems(PrintJobKey jobKey);

    // Job Priority
    void setPriority(PrintJobKey jobKey, int priority);

    void prioritizeJob(PrintJobKey jobKey);

    void deprioritizeJob(PrintJobKey jobKey);

    // Job Retrieval
    Optional<PrintJob> findByKey(PrintJobKey jobKey);

    List<PrintJob> findByPrinter(PrinterKey printerKey);

    List<PrintJob> findByQueue(PrintQueueKey queueKey);

    List<PrintJob> findByStatus(PrintJob.PrintJobStatus status, WarehouseKey warehouseKey);

    List<PrintJob> findByUser(UserKey userKey);

    List<PrintJob> findPendingJobs(WarehouseKey warehouseKey);

    List<PrintJob> findFailedJobs(WarehouseKey warehouseKey);

    List<PrintJob> findJobsInTimeRange(java.time.Instant from, java.time.Instant to, WarehouseKey warehouseKey);

    // Job Monitoring
    double getJobProgress(PrintJobKey jobKey);

    int getQueuePosition(PrintJobKey jobKey);

    java.time.Duration getEstimatedWaitTime(PrintJobKey jobKey);

    List<PrintJobItem> getFailedItems(PrintJobKey jobKey);

    // Batch Operations
    void cancelAllPendingJobs(PrinterKey printerKey, String reason, UserKey cancelledBy);

    void retryAllFailedJobs(PrinterKey printerKey);

    int purgeCompletedJobs(java.time.Instant olderThan, WarehouseKey warehouseKey);
}
