package com.maersk.wms.printing.domain.print_job.repository;

import com.maersk.wms.printing.domain.print_job.model.PrintJob;
import com.maersk.wms.printing.shared.kernel.identifiers.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for PrintJob persistence operations.
 */
public interface PrintJobRepository {

    PrintJob save(PrintJob printJob);

    List<PrintJob> saveAll(List<PrintJob> printJobs);

    Optional<PrintJob> findByKey(PrintJobKey jobKey);

    List<PrintJob> findByPrinterKey(PrinterKey printerKey);

    List<PrintJob> findByQueueKey(PrintQueueKey queueKey);

    List<PrintJob> findByWarehouseKey(WarehouseKey warehouseKey);

    List<PrintJob> findByStatus(PrintJob.PrintJobStatus status, WarehouseKey warehouseKey);

    List<PrintJob> findByStatusIn(List<PrintJob.PrintJobStatus> statuses, WarehouseKey warehouseKey);

    List<PrintJob> findByRequestedBy(UserKey userKey);

    List<PrintJob> findByRequestedFrom(DeviceKey deviceKey);

    List<PrintJob> findBySourceTypeAndKey(String sourceType, String sourceKey);

    List<PrintJob> findPendingJobs(WarehouseKey warehouseKey);

    List<PrintJob> findQueuedJobs(PrinterKey printerKey);

    List<PrintJob> findProcessingJobs(PrinterKey printerKey);

    List<PrintJob> findFailedJobs(WarehouseKey warehouseKey);

    List<PrintJob> findScheduledJobs(Instant before);

    List<PrintJob> findByCreatedAtBetween(Instant from, Instant to, WarehouseKey warehouseKey);

    List<PrintJob> findByCompletedAtBetween(Instant from, Instant to, WarehouseKey warehouseKey);

    List<PrintJob> findRetryableJobs(WarehouseKey warehouseKey);

    void delete(PrintJobKey jobKey);

    int deleteCompletedJobsOlderThan(Instant threshold, WarehouseKey warehouseKey);

    boolean existsByKey(PrintJobKey jobKey);

    long countByStatus(PrintJob.PrintJobStatus status, WarehouseKey warehouseKey);

    long countByPrinterAndStatus(PrinterKey printerKey, PrintJob.PrintJobStatus status);

    long countByQueueKey(PrintQueueKey queueKey);

    long countCompletedToday(WarehouseKey warehouseKey);

    long countFailedToday(WarehouseKey warehouseKey);
}
