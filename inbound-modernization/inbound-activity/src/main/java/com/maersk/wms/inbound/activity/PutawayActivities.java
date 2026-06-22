package com.maersk.wms.inbound.activity;

import com.maersk.wms.inbound.domain.PutawayTask;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.util.List;

/**
 * Temporal activities for putaway operations.
 */
@ActivityInterface
public interface PutawayActivities {

    /**
     * Create putaway task.
     */
    @ActivityMethod
    PutawayTask createTask(PutawayTask task);

    /**
     * Assign task to user.
     */
    @ActivityMethod
    PutawayTask assignTask(String taskKey, String userId);

    /**
     * Start putaway task.
     */
    @ActivityMethod
    PutawayTask startTask(String taskKey, String userId);

    /**
     * Complete putaway task.
     */
    @ActivityMethod
    PutawayTask completeTask(String taskKey, String actualLocation, String actualLpn);

    /**
     * Short pick putaway task.
     */
    @ActivityMethod
    PutawayTask shortTask(String taskKey, int actualQty, String reason);

    /**
     * Determine optimal putaway location.
     */
    @ActivityMethod
    String determinePutawayLocation(String sku, String lot, int qty);

    /**
     * Validate putaway location.
     */
    @ActivityMethod
    boolean validateLocation(String location, String sku);

    /**
     * Move inventory to putaway location.
     */
    @ActivityMethod
    void moveInventory(String fromLocation, String fromLpn, String toLocation, String toLpn, int qty);

    /**
     * Get pending putaway tasks for user.
     */
    @ActivityMethod
    List<PutawayTask> getPendingTasks(String userId);

    /**
     * Cancel putaway task.
     */
    @ActivityMethod
    void cancelTask(String taskKey, String reason);
}
