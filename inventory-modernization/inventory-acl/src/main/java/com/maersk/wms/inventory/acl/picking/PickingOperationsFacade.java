package com.maersk.wms.inventory.acl.picking;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;

import java.util.List;
import java.util.Optional;

/**
 * Anti-Corruption Layer facade for Picking Operations Service.
 * Translates picking domain concepts to inventory domain.
 * Downstream service consuming allocation and inventory data.
 */
public interface PickingOperationsFacade {

    // ═══════════════════════════════════════════════════════════════
    // PICK TASK QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Get pick task details.
     */
    Optional<PickTaskInfo> getPickTaskInfo(String pickTaskKey);

    /**
     * Get pick tasks for order.
     */
    List<PickTaskInfo> getPickTasksForOrder(OrderKey orderKey);

    /**
     * Get pick tasks for allocation.
     */
    List<PickTaskInfo> getPickTasksForAllocation(AllocationKey allocationKey);

    /**
     * Get pending pick tasks for location.
     */
    List<PickTaskInfo> getPendingPickTasksForLocation(LocationKey locationKey);

    // ═══════════════════════════════════════════════════════════════
    // ALLOCATION INTEGRATION
    // ═══════════════════════════════════════════════════════════════

    /**
     * Request pick task creation for allocation.
     */
    String createPickTask(AllocationKey allocationKey, InventoryKey inventoryKey, Quantity quantity);

    /**
     * Cancel pick task.
     */
    void cancelPickTask(String pickTaskKey, String reason);

    /**
     * Confirm pick.
     */
    void confirmPick(String pickTaskKey, InventoryKey inventoryKey, Quantity pickedQuantity);

    // ═══════════════════════════════════════════════════════════════
    // SHORT PICK HANDLING
    // ═══════════════════════════════════════════════════════════════

    /**
     * Report short pick.
     */
    void reportShortPick(String pickTaskKey, InventoryKey inventoryKey,
                         Quantity expectedQuantity, Quantity actualQuantity, String reason);

    /**
     * Request repick.
     */
    String requestRepick(String originalPickTaskKey, Quantity shortQuantity);

    // ═══════════════════════════════════════════════════════════════
    // PICK CONFIRMATION CALLBACKS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Notify picking that inventory was picked.
     */
    void notifyInventoryPicked(String pickTaskKey, InventoryKey inventoryKey, Quantity quantity);

    /**
     * Notify picking that allocation was deallocated.
     */
    void notifyAllocationDeallocated(AllocationKey allocationKey, String reason);

    // ═══════════════════════════════════════════════════════════════
    // DTOs
    // ═══════════════════════════════════════════════════════════════

    record PickTaskInfo(
            String pickTaskKey,
            AllocationKey allocationKey,
            OrderKey orderKey,
            InventoryKey inventoryKey,
            SkuKey skuKey,
            LocationKey fromLocation,
            LocationKey toLocation,
            Quantity requestedQuantity,
            Quantity pickedQuantity,
            String status,
            String assignedUser,
            int priority
    ) {}
}
