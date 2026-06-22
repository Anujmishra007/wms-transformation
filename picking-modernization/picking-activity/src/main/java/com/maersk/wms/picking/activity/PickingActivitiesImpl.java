package com.maersk.wms.picking.activity;

import com.maersk.wms.picking.domain.PickTask;
import com.maersk.wms.picking.domain.PickConfirmation;
import com.maersk.wms.picking.domain.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implementation of picking activities.
 * Activities are atomic units that can be retried and compensated.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PickingActivitiesImpl implements PickingActivities {

    // These would be injected services
    // private final TaskRepository taskRepository;
    // private final InventoryService inventoryService;
    // private final EventPublisher eventPublisher;
    // private final LegacyBridge legacyBridge;

    @Override
    public void assignTask(String taskId, String userId) {
        log.info("Assigning task {} to user {}", taskId, userId);
        // taskRepository.assignTask(taskId, userId);
    }

    @Override
    public boolean validateLocation(String scannedLocation, PickTask task) {
        log.info("Validating location {} for task {}", scannedLocation, task.getTaskId());
        return scannedLocation != null &&
               scannedLocation.equalsIgnoreCase(task.getFromLocation());
    }

    @Override
    public boolean validateSku(String scannedSku, PickTask task) {
        log.info("Validating SKU {} for task {}", scannedSku, task.getTaskId());
        return scannedSku != null &&
               scannedSku.equalsIgnoreCase(task.getSku());
    }

    @Override
    public void recordInventoryMovement(PickTask task, PickConfirmation confirmation) {
        log.info("Recording inventory movement for task {}, qty {}",
                task.getTaskId(), confirmation.getPickedQty());
        // inventoryService.recordPick(task, confirmation);
    }

    @Override
    public void compensateInventoryMovement(PickTask task) {
        log.info("Compensating inventory movement for task {}", task.getTaskId());
        // inventoryService.reversePick(task);
    }

    @Override
    public void updateTaskStatus(String taskId, TaskStatus status) {
        log.info("Updating task {} status to {}", taskId, status);
        // taskRepository.updateStatus(taskId, status);
    }

    @Override
    public void publishPickCompletedEvent(PickTask task, PickConfirmation confirmation) {
        log.info("Publishing pick completed event for task {}", task.getTaskId());
        // eventPublisher.publish(PickCompletedEvent.from(task, confirmation));
    }

    @Override
    public void callLegacySP(String spName, PickTask task, PickConfirmation confirmation) {
        log.info("Calling legacy SP {} for parity testing", spName);
        // legacyBridge.callSP(spName, task, confirmation);
    }
}
