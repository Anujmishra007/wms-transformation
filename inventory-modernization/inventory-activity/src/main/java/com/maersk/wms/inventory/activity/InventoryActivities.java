package com.maersk.wms.inventory.activity;

import com.maersk.wms.inventory.domain.*;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import java.math.BigDecimal;

/**
 * Temporal activities for inventory operations.
 */
@ActivityInterface
public interface InventoryActivities {

    @ActivityMethod
    LotxLocxId getInventory(String lotxLocxIdKey);

    @ActivityMethod
    void updateInventoryQty(String lotxLocxIdKey, BigDecimal newQty);

    @ActivityMethod
    void createAdjustmentRecord(InventoryAdjustment adjustment);

    @ActivityMethod
    void applyAdjustment(InventoryAdjustment adjustment);

    @ActivityMethod
    void compensateAdjustment(InventoryAdjustment adjustment);

    @ActivityMethod
    void executeTransfer(InventoryTransfer transfer);

    @ActivityMethod
    void compensateTransfer(InventoryTransfer transfer);

    @ActivityMethod
    void applyHold(InventoryHold hold);

    @ActivityMethod
    void releaseHold(InventoryHold hold);

    @ActivityMethod
    void publishInventoryEvent(String eventType, Object payload);
}
