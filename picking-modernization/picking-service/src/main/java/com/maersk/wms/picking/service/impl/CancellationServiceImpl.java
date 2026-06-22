package com.maersk.wms.picking.service.impl;

import com.maersk.wms.picking.domain.cancellation_service.model.CancellationApprovalStatus;
import com.maersk.wms.picking.domain.cancellation_service.model.CancellationReasonCode;
import com.maersk.wms.picking.domain.cancellation_service.model.CancellationScope;
import com.maersk.wms.picking.domain.cancellation_service.model.PickCancellation;
import com.maersk.wms.picking.domain.cancellation_service.repository.PickCancellationRepository;
import com.maersk.wms.picking.domain.cancellation_service.service.CancellationService;
import com.maersk.wms.picking.domain.task_execution_service.model.PickTask;
import com.maersk.wms.picking.domain.task_execution_service.model.PickTaskStatus;
import com.maersk.wms.picking.domain.task_execution_service.repository.PickTaskRepository;
import com.maersk.wms.picking.acl.allocation.AllocationFacade;
import com.maersk.wms.picking.acl.order.OrderFacade;
import com.maersk.wms.picking.shared.kernel.identifiers.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of Cancellation Service.
 * Handles pick task and order cancellations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CancellationServiceImpl implements CancellationService {

    private final PickCancellationRepository cancellationRepository;
    private final PickTaskRepository taskRepository;
    private final AllocationFacade allocationFacade;
    private final OrderFacade orderFacade;

    // Cancellation Requests

    @Override
    @Transactional
    public PickCancellation requestTaskCancellation(PickTaskKey taskKey,
                                                     CancellationReasonCode reason, String notes) {
        log.info("Requesting task cancellation for {} - reason: {}", taskKey, reason);

        PickCancellation cancellation = PickCancellation.builder()
                .cancellationId(UUID.randomUUID().toString())
                .scope(CancellationScope.TASK)
                .taskKey(taskKey)
                .reasonCode(reason)
                .notes(notes)
                .approvalStatus(requiresApproval(reason, CancellationScope.TASK)
                        ? CancellationApprovalStatus.PENDING
                        : CancellationApprovalStatus.NOT_REQUIRED)
                .requestedTime(LocalDateTime.now())
                .build();

        return cancellationRepository.save(cancellation);
    }

    @Override
    @Transactional
    public PickCancellation requestDetailCancellation(PickDetailKey pickDetailKey,
                                                       CancellationReasonCode reason, String notes) {
        log.info("Requesting detail cancellation for {} - reason: {}", pickDetailKey, reason);

        PickCancellation cancellation = PickCancellation.builder()
                .cancellationId(UUID.randomUUID().toString())
                .scope(CancellationScope.DETAIL)
                .pickDetailKey(pickDetailKey)
                .reasonCode(reason)
                .notes(notes)
                .approvalStatus(requiresApproval(reason, CancellationScope.DETAIL)
                        ? CancellationApprovalStatus.PENDING
                        : CancellationApprovalStatus.NOT_REQUIRED)
                .requestedTime(LocalDateTime.now())
                .build();

        return cancellationRepository.save(cancellation);
    }

    @Override
    @Transactional
    public PickCancellation requestOrderCancellation(OrderKey orderKey,
                                                      CancellationReasonCode reason, String notes) {
        log.info("Requesting order cancellation for {} - reason: {}", orderKey, reason);

        PickCancellation cancellation = PickCancellation.builder()
                .cancellationId(UUID.randomUUID().toString())
                .scope(CancellationScope.ORDER)
                .orderKey(orderKey)
                .reasonCode(reason)
                .notes(notes)
                .approvalStatus(CancellationApprovalStatus.PENDING) // Order cancellations always require approval
                .requestedTime(LocalDateTime.now())
                .build();

        return cancellationRepository.save(cancellation);
    }

    @Override
    @Transactional
    public PickCancellation requestWaveCancellation(WaveKey waveKey,
                                                     CancellationReasonCode reason, String notes) {
        log.info("Requesting wave cancellation for {} - reason: {}", waveKey, reason);

        PickCancellation cancellation = PickCancellation.builder()
                .cancellationId(UUID.randomUUID().toString())
                .scope(CancellationScope.WAVE)
                .waveKey(waveKey)
                .reasonCode(reason)
                .notes(notes)
                .approvalStatus(CancellationApprovalStatus.PENDING) // Wave cancellations always require approval
                .requestedTime(LocalDateTime.now())
                .build();

        return cancellationRepository.save(cancellation);
    }

    @Override
    @Transactional
    public PickCancellation requestListCancellation(PickListKey listKey,
                                                     CancellationReasonCode reason, String notes) {
        log.info("Requesting list cancellation for {} - reason: {}", listKey, reason);

        PickCancellation cancellation = PickCancellation.builder()
                .cancellationId(UUID.randomUUID().toString())
                .scope(CancellationScope.LIST)
                .listKey(listKey)
                .reasonCode(reason)
                .notes(notes)
                .approvalStatus(requiresApproval(reason, CancellationScope.LIST)
                        ? CancellationApprovalStatus.PENDING
                        : CancellationApprovalStatus.NOT_REQUIRED)
                .requestedTime(LocalDateTime.now())
                .build();

        return cancellationRepository.save(cancellation);
    }

    // Approval Workflow

    @Override
    @Transactional
    public void submitForApproval(String cancellationId) {
        log.info("Submitting cancellation {} for approval", cancellationId);

        PickCancellation cancellation = getCancellation(cancellationId)
                .orElseThrow(() -> new IllegalArgumentException("Cancellation not found: " + cancellationId));

        cancellation.setApprovalStatus(CancellationApprovalStatus.PENDING);
        cancellationRepository.save(cancellation);
    }

    @Override
    @Transactional
    public void approveCancellation(String cancellationId, UserKey approvedBy, String notes) {
        log.info("Approving cancellation {} by {}", cancellationId, approvedBy);

        PickCancellation cancellation = getCancellation(cancellationId)
                .orElseThrow(() -> new IllegalArgumentException("Cancellation not found: " + cancellationId));

        cancellation.approve(approvedBy, notes);
        cancellationRepository.save(cancellation);

        // Execute the cancellation
        executeCancellation(cancellationId);
    }

    @Override
    @Transactional
    public void rejectCancellation(String cancellationId, UserKey rejectedBy, String rejectionReason) {
        log.info("Rejecting cancellation {} by {}", cancellationId, rejectedBy);

        PickCancellation cancellation = getCancellation(cancellationId)
                .orElseThrow(() -> new IllegalArgumentException("Cancellation not found: " + cancellationId));

        cancellation.reject(rejectedBy, rejectionReason);
        cancellationRepository.save(cancellation);
    }

    @Override
    public boolean requiresApproval(CancellationReasonCode reason, CancellationScope scope) {
        // Order and Wave cancellations always require approval
        if (scope == CancellationScope.ORDER || scope == CancellationScope.WAVE) {
            return true;
        }
        // Check if reason code requires approval
        return reason.isRequiresApproval();
    }

    // Cancellation Execution

    @Override
    @Transactional
    public void executeCancellation(String cancellationId) {
        log.info("Executing cancellation {}", cancellationId);

        PickCancellation cancellation = getCancellation(cancellationId)
                .orElseThrow(() -> new IllegalArgumentException("Cancellation not found: " + cancellationId));

        // Check if approval is required and granted
        if (cancellation.getApprovalStatus() == CancellationApprovalStatus.PENDING) {
            throw new IllegalStateException("Cancellation requires approval");
        }
        if (cancellation.getApprovalStatus() == CancellationApprovalStatus.REJECTED) {
            throw new IllegalStateException("Cancellation was rejected");
        }

        // Execute based on scope
        switch (cancellation.getScope()) {
            case TASK -> cancelTask(cancellation.getTaskKey(), cancellation.getReasonCode());
            case DETAIL -> cancelDetail(cancellation.getPickDetailKey(), cancellation.getReasonCode());
            case ORDER -> cancelOrder(cancellation.getOrderKey(), cancellation.getReasonCode());
            case WAVE -> cancelWave(cancellation.getWaveKey(), cancellation.getReasonCode());
            case LIST -> cancelList(cancellation.getListKey(), cancellation.getReasonCode());
        }

        cancellation.setExecutedTime(LocalDateTime.now());
        cancellationRepository.save(cancellation);
    }

    @Override
    @Transactional
    public void executeImmediateCancellation(PickTaskKey taskKey, CancellationReasonCode reason,
                                             UserKey cancelledBy) {
        log.info("Executing immediate cancellation for task {} by {}", taskKey, cancelledBy);

        if (!canCancelTask(taskKey)) {
            throw new IllegalStateException("Task cannot be cancelled: " + getCancellationBlockReason(taskKey));
        }

        cancelTask(taskKey, reason);

        // Record the cancellation
        PickCancellation cancellation = PickCancellation.builder()
                .cancellationId(UUID.randomUUID().toString())
                .scope(CancellationScope.TASK)
                .taskKey(taskKey)
                .reasonCode(reason)
                .approvalStatus(CancellationApprovalStatus.NOT_REQUIRED)
                .requestedBy(cancelledBy)
                .requestedTime(LocalDateTime.now())
                .executedTime(LocalDateTime.now())
                .build();

        cancellationRepository.save(cancellation);
    }

    @Override
    @Transactional
    public void executeBatchCancellation(List<PickTaskKey> taskKeys, CancellationReasonCode reason,
                                         UserKey cancelledBy) {
        log.info("Executing batch cancellation for {} tasks by {}", taskKeys.size(), cancelledBy);

        for (PickTaskKey taskKey : taskKeys) {
            if (canCancelTask(taskKey)) {
                executeImmediateCancellation(taskKey, reason, cancelledBy);
            } else {
                log.warn("Skipping task {} - cannot cancel: {}", taskKey, getCancellationBlockReason(taskKey));
            }
        }
    }

    // Private helper methods

    private void cancelTask(PickTaskKey taskKey, CancellationReasonCode reason) {
        PickTask task = taskRepository.findById(taskKey)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskKey));

        task.setStatus(PickTaskStatus.CANCELLED);
        taskRepository.save(task);

        // Deallocate inventory
        if (task.getPickDetailKey() != null) {
            deallocateInventory(task.getPickDetailKey().toString());
        }
    }

    private void cancelDetail(PickDetailKey pickDetailKey, CancellationReasonCode reason) {
        allocationFacade.deallocatePickDetail(pickDetailKey, reason.getDescription());
    }

    private void cancelOrder(OrderKey orderKey, CancellationReasonCode reason) {
        // Cancel all tasks for the order
        List<PickTask> tasks = taskRepository.findByOrder(orderKey);
        for (PickTask task : tasks) {
            if (task.canCancel()) {
                cancelTask(task.getPickTaskKey(), reason);
            }
        }
        // Notify order service
        orderFacade.notifyOrderCancelled(orderKey, reason.getDescription());
    }

    private void cancelWave(WaveKey waveKey, CancellationReasonCode reason) {
        // Cancel all tasks for the wave
        List<PickTask> tasks = taskRepository.findByWave(waveKey);
        for (PickTask task : tasks) {
            if (task.canCancel()) {
                cancelTask(task.getPickTaskKey(), reason);
            }
        }
    }

    private void cancelList(PickListKey listKey, CancellationReasonCode reason) {
        // Cancel all tasks in the list
        List<PickTask> tasks = taskRepository.findByPickList(listKey);
        for (PickTask task : tasks) {
            if (task.canCancel()) {
                cancelTask(task.getPickTaskKey(), reason);
            }
        }
    }

    // Deallocation

    @Override
    @Transactional
    public void deallocateInventory(String cancellationId) {
        log.info("Deallocating inventory for cancellation {}", cancellationId);

        PickCancellation cancellation = getCancellation(cancellationId)
                .orElseThrow(() -> new IllegalArgumentException("Cancellation not found: " + cancellationId));

        if (cancellation.getPickDetailKey() != null) {
            allocationFacade.deallocatePickDetail(cancellation.getPickDetailKey(),
                    cancellation.getReasonCode().getDescription());
        }
    }

    @Override
    @Transactional
    public void returnToAvailable(PickDetailKey pickDetailKey) {
        log.info("Returning inventory to available for {}", pickDetailKey);
        allocationFacade.deallocatePickDetail(pickDetailKey, "Returned to available");
    }

    @Override
    @Transactional
    public void triggerReallocation(String cancellationId, boolean sameOrder) {
        log.info("Triggering reallocation for cancellation {} - same order: {}", cancellationId, sameOrder);

        PickCancellation cancellation = getCancellation(cancellationId)
                .orElseThrow(() -> new IllegalArgumentException("Cancellation not found: " + cancellationId));

        if (cancellation.getPickDetailKey() != null) {
            allocationFacade.reallocatePickDetail(cancellation.getPickDetailKey());
        }
    }

    // Query

    @Override
    public Optional<PickCancellation> getCancellation(String cancellationId) {
        return cancellationRepository.findById(cancellationId);
    }

    @Override
    public List<PickCancellation> getCancellationsByOrder(OrderKey orderKey) {
        return cancellationRepository.findByOrder(orderKey);
    }

    @Override
    public List<PickCancellation> getCancellationsByWave(WaveKey waveKey) {
        return cancellationRepository.findByWave(waveKey);
    }

    @Override
    public List<PickCancellation> getPendingApprovals(UserKey approverId) {
        return cancellationRepository.findPendingApprovalForUser(approverId);
    }

    @Override
    public List<PickCancellation> getCancellationsByStatus(CancellationApprovalStatus status) {
        return cancellationRepository.findByApprovalStatus(status);
    }

    @Override
    public List<PickCancellation> getCancellationHistory(LocalDateTime from, LocalDateTime to) {
        return cancellationRepository.findByDateRange(from, to);
    }

    // Validation

    @Override
    public boolean canCancelTask(PickTaskKey taskKey) {
        return taskRepository.findById(taskKey)
                .map(PickTask::canCancel)
                .orElse(false);
    }

    @Override
    public boolean canCancelOrder(OrderKey orderKey) {
        List<PickTask> tasks = taskRepository.findByOrder(orderKey);
        return tasks.stream().anyMatch(PickTask::canCancel);
    }

    @Override
    public String getCancellationBlockReason(PickTaskKey taskKey) {
        return taskRepository.findById(taskKey)
                .map(task -> {
                    if (task.getStatus() == PickTaskStatus.COMPLETED) {
                        return "Task is already completed";
                    }
                    if (task.getStatus() == PickTaskStatus.CANCELLED) {
                        return "Task is already cancelled";
                    }
                    if (task.getStatus() == PickTaskStatus.IN_PROGRESS &&
                        task.getQtyPicked().compareTo(java.math.BigDecimal.ZERO) > 0) {
                        return "Task has partial picks - use short handling instead";
                    }
                    return null;
                })
                .orElse("Task not found");
    }

    // Rollback

    @Override
    @Transactional
    public void rollbackCancellation(String cancellationId, String reason) {
        log.info("Rolling back cancellation {} - reason: {}", cancellationId, reason);

        if (!canRollback(cancellationId)) {
            throw new IllegalStateException("Cancellation cannot be rolled back");
        }

        PickCancellation cancellation = getCancellation(cancellationId)
                .orElseThrow(() -> new IllegalArgumentException("Cancellation not found: " + cancellationId));

        // Trigger reallocation to restore the cancelled picks
        triggerReallocation(cancellationId, true);

        cancellation.setRolledBack(true);
        cancellation.setRollbackReason(reason);
        cancellation.setRollbackTime(LocalDateTime.now());
        cancellationRepository.save(cancellation);
    }

    @Override
    public boolean canRollback(String cancellationId) {
        return getCancellation(cancellationId)
                .map(c -> c.getExecutedTime() != null &&
                          !c.isRolledBack() &&
                          c.getExecutedTime().isAfter(LocalDateTime.now().minusHours(24)))
                .orElse(false);
    }
}
