package com.maersk.wms.inbound.service.operations_service;

import com.maersk.wms.inbound.domain.operations_service.*;
import com.maersk.wms.inbound.domain.operations_service.repository.PutawayTaskRepository;
import com.maersk.wms.inbound.service.operations_service.dto.*;
import com.maersk.wms.inbound.shared.kernel.identifiers.*;
import com.maersk.wms.inbound.shared.kernel.valueobjects.Quantity;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service for Putaway Task execution (Putaway to Location).
 * Part of inbound-operations-service subdomain (operations/).
 *
 * Responsibilities:
 * - Create putaway tasks after receiving
 * - Assign tasks to users
 * - Execute putaway operations
 * - Track task completion
 * - Handle short putaway scenarios
 */
@Service
@Transactional
public class PutawayTaskService {

    private final PutawayTaskRepository putawayTaskRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PutawayTaskService(PutawayTaskRepository putawayTaskRepository,
                              ApplicationEventPublisher eventPublisher) {
        this.putawayTaskRepository = putawayTaskRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Create putaway task.
     */
    public PutawayTask createTask(CreatePutawayTaskRequest request) {
        validateCreateRequest(request);

        PutawayTask task = PutawayTask.builder()
            .storerKey(new StorerKey(request.getStorerKey()))
            .receiptKey(new ReceiptKey(request.getReceiptKey()))
            .sourceLpn(request.getSourceLpn() != null ? new LpnKey(request.getSourceLpn()) : null)
            .skuKey(new SkuKey(request.getStorerKey(), request.getSku()))
            .quantity(new Quantity(request.getQuantity(), request.getUom()))
            .fromLocation(new LocationKey(request.getFromLocation()))
            .suggestedLocation(request.getSuggestedLocation() != null
                ? new LocationKey(request.getSuggestedLocation()) : null)
            .taskType(request.getTaskType())
            .putawayStrategy(request.getPutawayStrategy())
            .putawayZone(request.getPutawayZone())
            .priority(request.getPriority())
            .crossdock(request.isCrossdock())
            .build();

        PutawayTask saved = putawayTaskRepository.save(task);

        eventPublisher.publishEvent(new PutawayTaskCreatedEvent(saved.getPutawayKey()));
        return saved;
    }

    /**
     * Create putaway tasks for receipt.
     */
    public List<PutawayTask> createTasksForReceipt(ReceiptKey receiptKey,
                                                    List<CreatePutawayTaskRequest> requests) {
        List<PutawayTask> tasks = requests.stream()
            .map(this::createTask)
            .toList();

        eventPublisher.publishEvent(new PutawayTasksCreatedForReceiptEvent(receiptKey, tasks.size()));
        return tasks;
    }

    /**
     * Get task by key.
     */
    @Transactional(readOnly = true)
    public Optional<PutawayTask> getTask(String putawayKey) {
        return putawayTaskRepository.findByKey(putawayKey);
    }

    /**
     * Get tasks by receipt.
     */
    @Transactional(readOnly = true)
    public List<PutawayTask> getByReceipt(ReceiptKey receiptKey) {
        return putawayTaskRepository.findByReceiptKey(receiptKey);
    }

    /**
     * Get tasks by status.
     */
    @Transactional(readOnly = true)
    public List<PutawayTask> getByStatus(PutawayTaskStatus status) {
        return putawayTaskRepository.findByStatus(status);
    }

    /**
     * Get unassigned tasks.
     */
    @Transactional(readOnly = true)
    public List<PutawayTask> getUnassignedTasks() {
        return putawayTaskRepository.findUnassigned();
    }

    /**
     * Get tasks assigned to user.
     */
    @Transactional(readOnly = true)
    public List<PutawayTask> getTasksForUser(String userId) {
        return putawayTaskRepository.findByAssignedUser(userId);
    }

    /**
     * Get pending tasks by priority.
     */
    @Transactional(readOnly = true)
    public List<PutawayTask> getPendingByPriority() {
        return putawayTaskRepository.findPendingByPriority();
    }

    /**
     * Get tasks by zone.
     */
    @Transactional(readOnly = true)
    public List<PutawayTask> getByZone(String zone) {
        return putawayTaskRepository.findByZone(zone);
    }

    /**
     * Get crossdock tasks.
     */
    @Transactional(readOnly = true)
    public List<PutawayTask> getCrossdockTasks() {
        return putawayTaskRepository.findCrossdockTasks();
    }

    /**
     * Assign task to user.
     */
    public PutawayTask assignTask(String putawayKey, String userId, String equipment) {
        PutawayTask task = putawayTaskRepository.findByKey(putawayKey)
            .orElseThrow(() -> new IllegalArgumentException("Task not found: " + putawayKey));

        task.assign(userId, equipment);
        PutawayTask saved = putawayTaskRepository.save(task);

        eventPublisher.publishEvent(new PutawayTaskAssignedEvent(putawayKey, userId));
        return saved;
    }

    /**
     * Start task execution.
     */
    public PutawayTask startTask(String putawayKey) {
        PutawayTask task = putawayTaskRepository.findByKey(putawayKey)
            .orElseThrow(() -> new IllegalArgumentException("Task not found: " + putawayKey));

        task.start();
        PutawayTask saved = putawayTaskRepository.save(task);

        eventPublisher.publishEvent(new PutawayTaskStartedEvent(putawayKey));
        return saved;
    }

    /**
     * Complete putaway task.
     */
    public PutawayResult completeTask(String putawayKey, CompletePutawayRequest request) {
        PutawayTask task = putawayTaskRepository.findByKey(putawayKey)
            .orElseThrow(() -> new IllegalArgumentException("Task not found: " + putawayKey));

        LocationKey actualLocation = new LocationKey(request.getActualLocation());
        task.complete(actualLocation, request.getCompletedBy());

        // Update target LPN if provided
        if (request.getTargetLpn() != null) {
            task.setTargetLpn(new LpnKey(request.getTargetLpn()));
        }

        PutawayTask saved = putawayTaskRepository.save(task);

        eventPublisher.publishEvent(new PutawayTaskCompletedEvent(putawayKey, actualLocation));

        PutawayResult result = new PutawayResult();
        result.setPutawayKey(putawayKey);
        result.setActualLocation(request.getActualLocation());
        result.setQuantity(task.getQuantity().getValue());
        result.setSuccess(true);
        return result;
    }

    /**
     * Confirm suggested location.
     */
    public PutawayTask confirmSuggestedLocation(String putawayKey) {
        PutawayTask task = putawayTaskRepository.findByKey(putawayKey)
            .orElseThrow(() -> new IllegalArgumentException("Task not found: " + putawayKey));

        task.confirmSuggestedLocation();
        return putawayTaskRepository.save(task);
    }

    /**
     * Override location.
     */
    public PutawayTask overrideLocation(String putawayKey, String newLocation, String reason) {
        PutawayTask task = putawayTaskRepository.findByKey(putawayKey)
            .orElseThrow(() -> new IllegalArgumentException("Task not found: " + putawayKey));

        task.overrideLocation(new LocationKey(newLocation), reason);
        return putawayTaskRepository.save(task);
    }

    /**
     * Record short putaway.
     */
    public PutawayTask shortPutaway(String putawayKey, ShortPutawayRequest request) {
        PutawayTask task = putawayTaskRepository.findByKey(putawayKey)
            .orElseThrow(() -> new IllegalArgumentException("Task not found: " + putawayKey));

        Quantity actualQty = new Quantity(request.getActualQuantity(), request.getUom());
        task.shortPutaway(actualQty, request.getReason());

        PutawayTask saved = putawayTaskRepository.save(task);

        eventPublisher.publishEvent(new ShortPutawayEvent(putawayKey, actualQty));
        return saved;
    }

    /**
     * Cancel task.
     */
    public PutawayTask cancelTask(String putawayKey, String reason) {
        PutawayTask task = putawayTaskRepository.findByKey(putawayKey)
            .orElseThrow(() -> new IllegalArgumentException("Task not found: " + putawayKey));

        task.cancel(reason);
        PutawayTask saved = putawayTaskRepository.save(task);

        eventPublisher.publishEvent(new PutawayTaskCancelledEvent(putawayKey, reason));
        return saved;
    }

    /**
     * Get next task for user by zone.
     */
    @Transactional(readOnly = true)
    public Optional<PutawayTask> getNextTaskForUser(String userId, String zone) {
        List<PutawayTask> tasks = putawayTaskRepository.findByZone(zone).stream()
            .filter(t -> t.getStatus() == PutawayTaskStatus.PENDING)
            .sorted((a, b) -> Integer.compare(b.getPriority(), a.getPriority()))
            .toList();

        return tasks.isEmpty() ? Optional.empty() : Optional.of(tasks.get(0));
    }

    /**
     * Get task statistics by zone.
     */
    @Transactional(readOnly = true)
    public PutawayTaskStats getStatsByZone(String zone) {
        PutawayTaskStats stats = new PutawayTaskStats();
        stats.setZone(zone);
        stats.setPendingCount(putawayTaskRepository.countByZoneAndStatus(zone, PutawayTaskStatus.PENDING));
        stats.setInProgressCount(putawayTaskRepository.countByZoneAndStatus(zone, PutawayTaskStatus.IN_PROGRESS));
        stats.setCompletedCount(putawayTaskRepository.countByZoneAndStatus(zone, PutawayTaskStatus.COMPLETED));
        return stats;
    }

    private void validateCreateRequest(CreatePutawayTaskRequest request) {
        if (request.getStorerKey() == null || request.getStorerKey().isBlank()) {
            throw new IllegalArgumentException("Storer key is required");
        }
        if (request.getReceiptKey() == null || request.getReceiptKey().isBlank()) {
            throw new IllegalArgumentException("Receipt key is required");
        }
        if (request.getSku() == null || request.getSku().isBlank()) {
            throw new IllegalArgumentException("SKU is required");
        }
        if (request.getQuantity() == null || request.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }

    // Event classes
    public record PutawayTaskCreatedEvent(String putawayKey) {}
    public record PutawayTasksCreatedForReceiptEvent(ReceiptKey receiptKey, int taskCount) {}
    public record PutawayTaskAssignedEvent(String putawayKey, String userId) {}
    public record PutawayTaskStartedEvent(String putawayKey) {}
    public record PutawayTaskCompletedEvent(String putawayKey, LocationKey location) {}
    public record ShortPutawayEvent(String putawayKey, Quantity actualQty) {}
    public record PutawayTaskCancelledEvent(String putawayKey, String reason) {}
}
