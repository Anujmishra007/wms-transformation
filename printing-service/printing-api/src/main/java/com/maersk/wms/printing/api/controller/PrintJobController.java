package com.maersk.wms.printing.api.controller;

import com.maersk.wms.printing.domain.print_job.model.PrintJob;
import com.maersk.wms.printing.domain.print_job.model.PrintJobItem;
import com.maersk.wms.printing.domain.print_job.service.PrintJobService;
import com.maersk.wms.printing.shared.kernel.identifiers.*;
import com.maersk.wms.printing.shared.kernel.valueobjects.PrintSettings;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

/**
 * REST controller for print job operations.
 */
@RestController
@RequestMapping("/api/v1/print-jobs")
public class PrintJobController {

    private final PrintJobService printJobService;

    public PrintJobController(PrintJobService printJobService) {
        this.printJobService = printJobService;
    }

    @PostMapping
    public ResponseEntity<PrintJobResponse> createJob(@RequestBody CreatePrintJobRequest request) {
        PrintSettings settings = request.settings() != null ?
                new PrintSettings(
                        request.settings().copies(),
                        request.settings().dpi() > 0 ? request.settings().dpi() : 203,
                        "LABEL",                // mediaType
                        "PORTRAIT",             // orientation
                        false,                  // collate
                        request.settings().darkness(),
                        request.settings().speed()
                ) : PrintSettings.defaults();

        PrintJob job = printJobService.createJobWithLabels(
                request.jobName(),
                request.labelKeys().stream().map(LabelKey::new).toList(),
                new PrinterKey(request.printerKey()),
                settings,
                new WarehouseKey(request.warehouseKey()),
                new UserKey(request.requestedBy()),
                request.deviceKey() != null ? new DeviceKey(request.deviceKey()) : null
        );

        return ResponseEntity.ok(mapToResponse(job));
    }

    @GetMapping("/{jobKey}")
    public ResponseEntity<PrintJobResponse> getJob(@PathVariable String jobKey) {
        return printJobService.findByKey(new PrintJobKey(jobKey))
                .map(this::mapToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{jobKey}/queue")
    public ResponseEntity<Void> queueJob(
            @PathVariable String jobKey,
            @RequestParam String queueKey) {
        printJobService.queueJob(new PrintJobKey(jobKey), new PrintQueueKey(queueKey));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{jobKey}/start")
    public ResponseEntity<Void> startJob(@PathVariable String jobKey) {
        printJobService.startJob(new PrintJobKey(jobKey));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{jobKey}/complete")
    public ResponseEntity<Void> completeJob(@PathVariable String jobKey) {
        printJobService.completeJob(new PrintJobKey(jobKey));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{jobKey}/fail")
    public ResponseEntity<Void> failJob(
            @PathVariable String jobKey,
            @RequestBody FailJobRequest request) {
        printJobService.failJob(new PrintJobKey(jobKey), request.error());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{jobKey}/cancel")
    public ResponseEntity<Void> cancelJob(
            @PathVariable String jobKey,
            @RequestBody CancelJobRequest request) {
        printJobService.cancelJob(
                new PrintJobKey(jobKey),
                request.reason(),
                new UserKey(request.cancelledBy())
        );
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{jobKey}/pause")
    public ResponseEntity<Void> pauseJob(@PathVariable String jobKey) {
        printJobService.pauseJob(new PrintJobKey(jobKey));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{jobKey}/resume")
    public ResponseEntity<Void> resumeJob(@PathVariable String jobKey) {
        printJobService.resumeJob(new PrintJobKey(jobKey));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{jobKey}/retry")
    public ResponseEntity<Void> retryJob(@PathVariable String jobKey) {
        printJobService.retryJob(new PrintJobKey(jobKey));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{jobKey}/items/{labelKey}/printed")
    public ResponseEntity<Void> recordItemPrinted(
            @PathVariable String jobKey,
            @PathVariable String labelKey) {
        printJobService.recordItemPrinted(new PrintJobKey(jobKey), new LabelKey(labelKey));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{jobKey}/items/{labelKey}/failed")
    public ResponseEntity<Void> recordItemFailed(
            @PathVariable String jobKey,
            @PathVariable String labelKey,
            @RequestBody ItemFailedRequest request) {
        printJobService.recordItemFailed(new PrintJobKey(jobKey), new LabelKey(labelKey), request.error());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{jobKey}/progress")
    public ResponseEntity<JobProgressResponse> getJobProgress(@PathVariable String jobKey) {
        double progress = printJobService.getJobProgress(new PrintJobKey(jobKey));
        int position = printJobService.getQueuePosition(new PrintJobKey(jobKey));
        Duration waitTime = printJobService.getEstimatedWaitTime(new PrintJobKey(jobKey));

        return ResponseEntity.ok(new JobProgressResponse(progress, position, waitTime.toMinutes()));
    }

    @GetMapping("/{jobKey}/failed-items")
    public ResponseEntity<List<PrintJobItemResponse>> getFailedItems(@PathVariable String jobKey) {
        List<PrintJobItem> items = printJobService.getFailedItems(new PrintJobKey(jobKey));
        return ResponseEntity.ok(items.stream().map(this::mapToItemResponse).toList());
    }

    @GetMapping("/printer/{printerKey}")
    public ResponseEntity<List<PrintJobResponse>> getJobsByPrinter(@PathVariable String printerKey) {
        List<PrintJob> jobs = printJobService.findByPrinter(new PrinterKey(printerKey));
        return ResponseEntity.ok(jobs.stream().map(this::mapToResponse).toList());
    }

    @GetMapping("/queue/{queueKey}")
    public ResponseEntity<List<PrintJobResponse>> getJobsByQueue(@PathVariable String queueKey) {
        List<PrintJob> jobs = printJobService.findByQueue(new PrintQueueKey(queueKey));
        return ResponseEntity.ok(jobs.stream().map(this::mapToResponse).toList());
    }

    @GetMapping("/pending/{warehouseKey}")
    public ResponseEntity<List<PrintJobResponse>> getPendingJobs(@PathVariable String warehouseKey) {
        List<PrintJob> jobs = printJobService.findPendingJobs(new WarehouseKey(warehouseKey));
        return ResponseEntity.ok(jobs.stream().map(this::mapToResponse).toList());
    }

    @GetMapping("/failed/{warehouseKey}")
    public ResponseEntity<List<PrintJobResponse>> getFailedJobs(@PathVariable String warehouseKey) {
        List<PrintJob> jobs = printJobService.findFailedJobs(new WarehouseKey(warehouseKey));
        return ResponseEntity.ok(jobs.stream().map(this::mapToResponse).toList());
    }

    // DTOs
    public record CreatePrintJobRequest(
            String jobName,
            List<String> labelKeys,
            String printerKey,
            PrintSettingsDto settings,
            String warehouseKey,
            String requestedBy,
            String deviceKey
    ) {}

    public record PrintSettingsDto(
            int copies,
            int dpi,
            int darkness,
            int speed,
            int rotation
    ) {}

    public record FailJobRequest(String error) {}

    public record CancelJobRequest(String reason, String cancelledBy) {}

    public record ItemFailedRequest(String error) {}

    public record PrintJobResponse(
            String jobKey,
            String jobName,
            String printerKey,
            String queueKey,
            String status,
            int totalItems,
            int printedItems,
            int failedItems,
            double progress,
            int priority,
            String warehouseKey,
            String requestedBy,
            String createdAt
    ) {}

    public record PrintJobItemResponse(
            String itemKey,
            String labelKey,
            int sequenceNumber,
            int copies,
            String status,
            String errorMessage
    ) {}

    public record JobProgressResponse(
            double progress,
            int queuePosition,
            long estimatedWaitMinutes
    ) {}

    // Helper methods
    private PrintJobResponse mapToResponse(PrintJob job) {
        return new PrintJobResponse(
                job.getPrintJobKey().value(),
                job.getJobName(),
                job.getPrinterKey() != null ? job.getPrinterKey().value() : null,
                job.getQueueKey() != null ? job.getQueueKey().value() : null,
                job.getStatus().name(),
                job.getTotalItems(),
                job.getPrintedItems(),
                job.getFailedItems(),
                job.getProgress(),
                job.getPriority(),
                job.getWarehouseKey().value(),
                job.getRequestedBy() != null ? job.getRequestedBy().value() : null,
                job.getCreatedAt().toString()
        );
    }

    private PrintJobItemResponse mapToItemResponse(PrintJobItem item) {
        return new PrintJobItemResponse(
                item.getItemKey(),
                item.getLabelKey().value(),
                item.getSequenceNumber(),
                item.getCopies(),
                item.getStatus().name(),
                item.getErrorMessage()
        );
    }
}
