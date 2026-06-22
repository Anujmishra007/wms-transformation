package com.maersk.wms.picking.activity;

import com.maersk.wms.picking.domain.PickTask;
import com.maersk.wms.picking.domain.PickConfirmation;
import com.maersk.wms.picking.domain.TaskStatus;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Temporal activities interface for picking operations.
 * Each activity is an atomic, compensatable unit of work.
 */
@ActivityInterface
public interface PickingActivities {

    /**
     * Assign task to user.
     */
    @ActivityMethod
    void assignTask(String taskId, String userId);

    /**
     * Validate scanned location against task.
     */
    @ActivityMethod
    boolean validateLocation(String scannedLocation, PickTask task);

    /**
     * Validate scanned SKU against task.
     */
    @ActivityMethod
    boolean validateSku(String scannedSku, PickTask task);

    /**
     * Record inventory movement (pick from location).
     * This is a compensatable activity.
     */
    @ActivityMethod
    void recordInventoryMovement(PickTask task, PickConfirmation confirmation);

    /**
     * Compensate inventory movement (reverse pick).
     */
    @ActivityMethod
    void compensateInventoryMovement(PickTask task);

    /**
     * Update task status.
     */
    @ActivityMethod
    void updateTaskStatus(String taskId, TaskStatus status);

    /**
     * Publish pick completed event.
     */
    @ActivityMethod
    void publishPickCompletedEvent(PickTask task, PickConfirmation confirmation);

    /**
     * Call legacy SP for parity testing.
     */
    @ActivityMethod
    void callLegacySP(String spName, PickTask task, PickConfirmation confirmation);
}
