package com.maersk.wms.printing.service.job;

import com.maersk.wms.printing.domain.print_job.model.PrintJob;
import com.maersk.wms.printing.domain.print_job.model.PrintJobItem;
import com.maersk.wms.printing.domain.print_job.repository.PrintJobRepository;
import com.maersk.wms.printing.domain.print_job.service.PrintJobService;
import com.maersk.wms.printing.domain.print_job.event.PrintJobEvents.*;
import com.maersk.wms.printing.domain.printer_management.model.PrintQueue;
import com.maersk.wms.printing.domain.printer_management.repository.PrintQueueRepository;
import com.maersk.wms.printing.shared.kernel.identifiers.*;
import com.maersk.wms.printing.shared.kernel.valueobjects.PrintSettings;
import com.maersk.wms.printing.shared.kernel.exceptions.*;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of PrintJobService.
 * Handles job creation, queuing, execution, and monitoring.
 */
@Service
@Transactional
public class PrintJobServiceImpl implements PrintJobService {

    private final PrintJobRepository jobRepository;
    private final PrintQueueRepository queueRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PrintJobServiceImpl(
            PrintJobRepository jobRepository,
            PrintQueueRepository queueRepository,
            ApplicationEventPublisher eventPublisher) {
        this.jobRepository = jobRepository;
        this.queueRepository = queueRepository;
        this.eventPublisher = eventPublisher;
    }

    // Job Creation
    @Override
    public PrintJob createJob(String jobName, PrinterKey printerKey, WarehouseKey warehouseKey,
                              UserKey requestedBy, DeviceKey requestedFrom) {
        PrintJob job = PrintJob.builder()
                .printJobKey(new PrintJobKey(UUID.randomUUID().toString()))
                .jobName(jobName)
                .printerKey(printerKey)
                .warehouseKey(warehouseKey)
                .requestedBy(requestedBy)
                .requestedFrom(requestedFrom)
                .status(PrintJob.PrintJobStatus.CREATED)
                .priority(5) // Default priority
                .maxRetries(3)
                .createdAt(Instant.now())
                .createdBy(requestedBy.value())
                .build();

        PrintJob saved = jobRepository.save(job);

        eventPublisher.publishEvent(new PrintJobCreated(
                saved.getPrintJobKey(),
                jobName,
                printerKey,
                0,
                saved.getSourceType(),
                warehouseKey,
                requestedBy,
                requestedFrom,
                Instant.now()
        ));

        return saved;
    }

    @Override
    public PrintJob createJobWithLabels(String jobName, List<LabelKey> labelKeys, PrinterKey printerKey,
                                         PrintSettings settings, WarehouseKey warehouseKey,
                                         UserKey requestedBy, DeviceKey requestedFrom) {
        PrintJob job = createJob(jobName, printerKey, warehouseKey, requestedBy, requestedFrom);
        job.setSettings(settings);

        int sequence = 1;
        for (LabelKey labelKey : labelKeys) {
            PrintJobItem item = PrintJobItem.builder()
                    .itemKey(UUID.randomUUID().toString())
                    .printJobKey(job.getPrintJobKey())
                    .labelKey(labelKey)
                    .sequenceNumber(sequence++)
                    .copies(settings != null ? settings.copies() : 1)
                    .status(PrintJobItem.PrintItemStatus.PENDING)
                    .build();
            job.addItem(item);
        }

        return jobRepository.save(job);
    }

    @Override
    public PrintJob createScheduledJob(String jobName, List<LabelKey> labelKeys, PrinterKey printerKey,
                                        PrintSettings settings, Instant scheduledAt,
                                        WarehouseKey warehouseKey, UserKey requestedBy) {
        PrintJob job = createJobWithLabels(jobName, labelKeys, printerKey, settings, warehouseKey, requestedBy, null);
        job.setScheduledAt(scheduledAt);
        return jobRepository.save(job);
    }

    // Job Items
    @Override
    public void addItem(PrintJobKey jobKey, LabelKey labelKey, int copies) {
        PrintJob job = findByKeyOrThrow(jobKey);

        PrintJobItem item = PrintJobItem.builder()
                .itemKey(UUID.randomUUID().toString())
                .printJobKey(jobKey)
                .labelKey(labelKey)
                .sequenceNumber(job.getItems().size() + 1)
                .copies(copies)
                .status(PrintJobItem.PrintItemStatus.PENDING)
                .build();

        job.addItem(item);
        jobRepository.save(job);

        eventPublisher.publishEvent(new PrintJobItemAdded(
                jobKey,
                labelKey,
                item.getSequenceNumber(),
                copies,
                Instant.now()
        ));
    }

    @Override
    public void addItems(PrintJobKey jobKey, List<LabelKey> labelKeys) {
        labelKeys.forEach(labelKey -> addItem(jobKey, labelKey, 1));
    }

    @Override
    public void removeItem(PrintJobKey jobKey, LabelKey labelKey) {
        PrintJob job = findByKeyOrThrow(jobKey);
        job.getItems().removeIf(item -> item.getLabelKey().equals(labelKey));
        jobRepository.save(job);

        eventPublisher.publishEvent(new PrintJobItemRemoved(jobKey, labelKey, Instant.now()));
    }

    @Override
    public void updateItemCopies(PrintJobKey jobKey, LabelKey labelKey, int copies) {
        PrintJob job = findByKeyOrThrow(jobKey);
        job.getItems().stream()
                .filter(item -> item.getLabelKey().equals(labelKey))
                .findFirst()
                .ifPresent(item -> item.setCopies(copies));
        jobRepository.save(job);
    }

    // Job Queuing
    @Override
    public void queueJob(PrintJobKey jobKey, PrintQueueKey queueKey) {
        PrintJob job = findByKeyOrThrow(jobKey);
        PrintQueue queue = queueRepository.findByKey(queueKey)
                .orElseThrow(() -> new PrintingException("Queue not found: " + queueKey.value()));

        if (!queue.isActive()) {
            throw new PrintingException("Queue is not active");
        }

        if (!queue.enqueue(jobKey)) {
            throw new PrintingException("Queue is full");
        }

        job.setQueueKey(queueKey);
        job.queue();

        queueRepository.save(queue);
        jobRepository.save(job);

        eventPublisher.publishEvent(new PrintJobQueued(
                jobKey,
                queueKey,
                queue.getPosition(jobKey),
                Instant.now()
        ));
    }

    @Override
    public void queueJobWithPriority(PrintJobKey jobKey, PrintQueueKey queueKey, int priority) {
        PrintJob job = findByKeyOrThrow(jobKey);
        job.setPriority(priority);
        jobRepository.save(job);
        queueJob(jobKey, queueKey);
    }

    @Override
    public void requeueJob(PrintJobKey jobKey) {
        PrintJob job = findByKeyOrThrow(jobKey);
        if (job.getQueueKey() != null) {
            queueJob(jobKey, job.getQueueKey());
        }
    }

    @Override
    public void moveToQueue(PrintJobKey jobKey, PrintQueueKey targetQueueKey) {
        PrintJob job = findByKeyOrThrow(jobKey);
        PrintQueueKey fromQueueKey = job.getQueueKey();

        // Remove from current queue
        if (fromQueueKey != null) {
            queueRepository.findByKey(fromQueueKey)
                    .ifPresent(queue -> {
                        queue.removeJob(jobKey);
                        queueRepository.save(queue);
                    });
        }

        // Add to new queue
        queueJob(jobKey, targetQueueKey);

        eventPublisher.publishEvent(new PrintJobMovedToQueue(
                jobKey,
                fromQueueKey,
                targetQueueKey,
                Instant.now()
        ));
    }

    // Job Execution
    @Override
    public void startJob(PrintJobKey jobKey) {
        PrintJob job = findByKeyOrThrow(jobKey);
        job.start();
        jobRepository.save(job);

        eventPublisher.publishEvent(new PrintJobStarted(
                jobKey,
                job.getPrinterKey(),
                Instant.now()
        ));
    }

    @Override
    public void completeJob(PrintJobKey jobKey) {
        PrintJob job = findByKeyOrThrow(jobKey);
        Instant startTime = job.getStartedAt();
        job.complete();
        jobRepository.save(job);

        // Complete in queue
        if (job.getQueueKey() != null) {
            queueRepository.findByKey(job.getQueueKey())
                    .ifPresent(queue -> {
                        queue.completeCurrentJob();
                        queueRepository.save(queue);
                    });
        }

        long durationMs = startTime != null ? Duration.between(startTime, Instant.now()).toMillis() : 0;

        eventPublisher.publishEvent(new PrintJobCompleted(
                jobKey,
                job.getPrintedItems(),
                job.getFailedItems(),
                durationMs,
                Instant.now()
        ));
    }

    @Override
    public void failJob(PrintJobKey jobKey, String error) {
        PrintJob job = findByKeyOrThrow(jobKey);
        job.fail(error);
        jobRepository.save(job);

        eventPublisher.publishEvent(new PrintJobFailed(
                jobKey,
                error,
                job.getRetryCount(),
                job.canRetry(),
                Instant.now()
        ));
    }

    @Override
    public void recordItemPrinted(PrintJobKey jobKey, LabelKey labelKey) {
        PrintJob job = findByKeyOrThrow(jobKey);
        job.recordItemPrinted(labelKey);
        jobRepository.save(job);

        eventPublisher.publishEvent(new PrintJobItemPrinted(
                jobKey,
                labelKey,
                getItemSequence(job, labelKey),
                Instant.now()
        ));
    }

    @Override
    public void recordItemFailed(PrintJobKey jobKey, LabelKey labelKey, String error) {
        PrintJob job = findByKeyOrThrow(jobKey);
        job.recordItemFailed(labelKey, error);
        jobRepository.save(job);

        eventPublisher.publishEvent(new PrintJobItemFailed(
                jobKey,
                labelKey,
                error,
                Instant.now()
        ));
    }

    // Job Control
    @Override
    public void pauseJob(PrintJobKey jobKey) {
        PrintJob job = findByKeyOrThrow(jobKey);
        job.pause();
        jobRepository.save(job);

        eventPublisher.publishEvent(new PrintJobPaused(
                jobKey,
                job.getPrintedItems(),
                job.getTotalItems() - job.getPrintedItems() - job.getFailedItems(),
                Instant.now()
        ));
    }

    @Override
    public void resumeJob(PrintJobKey jobKey) {
        PrintJob job = findByKeyOrThrow(jobKey);
        job.resume();
        jobRepository.save(job);

        eventPublisher.publishEvent(new PrintJobResumed(jobKey, Instant.now()));
    }

    @Override
    public void cancelJob(PrintJobKey jobKey, String reason, UserKey cancelledBy) {
        PrintJob job = findByKeyOrThrow(jobKey);
        job.cancel();
        jobRepository.save(job);

        // Remove from queue
        if (job.getQueueKey() != null) {
            queueRepository.findByKey(job.getQueueKey())
                    .ifPresent(queue -> {
                        queue.removeJob(jobKey);
                        queueRepository.save(queue);
                    });
        }

        eventPublisher.publishEvent(new PrintJobCancelled(
                jobKey,
                reason,
                cancelledBy,
                Instant.now()
        ));
    }

    @Override
    public void retryJob(PrintJobKey jobKey) {
        PrintJob job = findByKeyOrThrow(jobKey);
        if (!job.canRetry()) {
            throw new PrintingException("Job cannot be retried. Max retries exceeded.");
        }
        job.incrementRetry();
        jobRepository.save(job);

        eventPublisher.publishEvent(new PrintJobRetried(
                jobKey,
                job.getRetryCount(),
                job.getMaxRetries(),
                Instant.now()
        ));
    }

    @Override
    public void retryFailedItems(PrintJobKey jobKey) {
        PrintJob job = findByKeyOrThrow(jobKey);
        job.getItems().stream()
                .filter(PrintJobItem::isFailed)
                .forEach(item -> {
                    item.setStatus(PrintJobItem.PrintItemStatus.PENDING);
                    item.setErrorMessage(null);
                });
        job.setStatus(PrintJob.PrintJobStatus.QUEUED);
        jobRepository.save(job);
    }

    // Job Priority
    @Override
    public void setPriority(PrintJobKey jobKey, int priority) {
        PrintJob job = findByKeyOrThrow(jobKey);
        int previousPriority = job.getPriority();
        job.setPriority(priority);
        jobRepository.save(job);

        eventPublisher.publishEvent(new PrintJobPrioritized(
                jobKey,
                previousPriority,
                priority,
                Instant.now()
        ));
    }

    @Override
    public void prioritizeJob(PrintJobKey jobKey) {
        PrintJob job = findByKeyOrThrow(jobKey);
        job.setPriority(job.getPriority() + 10);
        jobRepository.save(job);

        if (job.getQueueKey() != null) {
            queueRepository.findByKey(job.getQueueKey())
                    .ifPresent(queue -> {
                        queue.prioritize(jobKey);
                        queueRepository.save(queue);
                    });
        }
    }

    @Override
    public void deprioritizeJob(PrintJobKey jobKey) {
        PrintJob job = findByKeyOrThrow(jobKey);
        job.setPriority(Math.max(1, job.getPriority() - 10));
        jobRepository.save(job);
    }

    // Job Retrieval
    @Override
    @Transactional(readOnly = true)
    public Optional<PrintJob> findByKey(PrintJobKey jobKey) {
        return jobRepository.findByKey(jobKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrintJob> findByPrinter(PrinterKey printerKey) {
        return jobRepository.findByPrinterKey(printerKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrintJob> findByQueue(PrintQueueKey queueKey) {
        return jobRepository.findByQueueKey(queueKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrintJob> findByStatus(PrintJob.PrintJobStatus status, WarehouseKey warehouseKey) {
        return jobRepository.findByStatus(status, warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrintJob> findByUser(UserKey userKey) {
        return jobRepository.findByRequestedBy(userKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrintJob> findPendingJobs(WarehouseKey warehouseKey) {
        return jobRepository.findPendingJobs(warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrintJob> findFailedJobs(WarehouseKey warehouseKey) {
        return jobRepository.findFailedJobs(warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrintJob> findJobsInTimeRange(Instant from, Instant to, WarehouseKey warehouseKey) {
        return jobRepository.findByCreatedAtBetween(from, to, warehouseKey);
    }

    // Job Monitoring
    @Override
    @Transactional(readOnly = true)
    public double getJobProgress(PrintJobKey jobKey) {
        return findByKeyOrThrow(jobKey).getProgress();
    }

    @Override
    @Transactional(readOnly = true)
    public int getQueuePosition(PrintJobKey jobKey) {
        PrintJob job = findByKeyOrThrow(jobKey);
        if (job.getQueueKey() == null) return -1;

        return queueRepository.findByKey(job.getQueueKey())
                .map(queue -> queue.getPosition(jobKey))
                .orElse(-1);
    }

    @Override
    @Transactional(readOnly = true)
    public Duration getEstimatedWaitTime(PrintJobKey jobKey) {
        // TODO: Implement based on queue depth and average print time
        int position = getQueuePosition(jobKey);
        if (position < 0) return Duration.ZERO;
        return Duration.ofMinutes(position * 2L); // Rough estimate: 2 minutes per job
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrintJobItem> getFailedItems(PrintJobKey jobKey) {
        return findByKeyOrThrow(jobKey).getItems().stream()
                .filter(PrintJobItem::isFailed)
                .collect(Collectors.toList());
    }

    // Batch Operations
    @Override
    public void cancelAllPendingJobs(PrinterKey printerKey, String reason, UserKey cancelledBy) {
        List<PrintJob> pendingJobs = jobRepository.findQueuedJobs(printerKey);
        pendingJobs.forEach(job -> cancelJob(job.getPrintJobKey(), reason, cancelledBy));
    }

    @Override
    public void retryAllFailedJobs(PrinterKey printerKey) {
        List<PrintJob> failedJobs = jobRepository.findByPrinterKey(printerKey).stream()
                .filter(job -> job.getStatus() == PrintJob.PrintJobStatus.FAILED && job.canRetry())
                .collect(Collectors.toList());
        failedJobs.forEach(job -> retryJob(job.getPrintJobKey()));
    }

    @Override
    public int purgeCompletedJobs(Instant olderThan, WarehouseKey warehouseKey) {
        return jobRepository.deleteCompletedJobsOlderThan(olderThan, warehouseKey);
    }

    // Helper Methods
    private PrintJob findByKeyOrThrow(PrintJobKey jobKey) {
        return jobRepository.findByKey(jobKey)
                .orElseThrow(() -> new PrintJobNotFoundException("Print job not found: " + jobKey.value()));
    }

    private int getItemSequence(PrintJob job, LabelKey labelKey) {
        return job.getItems().stream()
                .filter(item -> item.getLabelKey().equals(labelKey))
                .findFirst()
                .map(PrintJobItem::getSequenceNumber)
                .orElse(0);
    }
}
