package com.maersk.wms.inventory.acl.picking;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Adapter implementation for Picking Operations Service facade.
 * Communicates with picking-operations-service via REST/gRPC.
 */
@Component
public class PickingOperationsAdapter implements PickingOperationsFacade {

    private final WebClient pickingClient;

    public PickingOperationsAdapter(WebClient.Builder webClientBuilder) {
        this.pickingClient = webClientBuilder
                .baseUrl("${services.picking-operations.url:http://picking-operations-service}")
                .build();
    }

    // ═══════════════════════════════════════════════════════════════
    // PICK TASK QUERIES
    // ═══════════════════════════════════════════════════════════════

    @Override
    public Optional<PickTaskInfo> getPickTaskInfo(String pickTaskKey) {
        // TODO: Implement REST call to picking-operations-service
        // GET /api/v1/pick-tasks/{pickTaskKey}
        return Optional.empty();
    }

    @Override
    public List<PickTaskInfo> getPickTasksForOrder(OrderKey orderKey) {
        // TODO: Implement REST call
        // GET /api/v1/orders/{orderKey}/pick-tasks
        return Collections.emptyList();
    }

    @Override
    public List<PickTaskInfo> getPickTasksForAllocation(AllocationKey allocationKey) {
        // TODO: Implement REST call
        // GET /api/v1/allocations/{allocationKey}/pick-tasks
        return Collections.emptyList();
    }

    @Override
    public List<PickTaskInfo> getPendingPickTasksForLocation(LocationKey locationKey) {
        // TODO: Implement REST call
        // GET /api/v1/locations/{locationKey}/pick-tasks?status=PENDING
        return Collections.emptyList();
    }

    // ═══════════════════════════════════════════════════════════════
    // ALLOCATION INTEGRATION
    // ═══════════════════════════════════════════════════════════════

    @Override
    public String createPickTask(AllocationKey allocationKey, InventoryKey inventoryKey, Quantity quantity) {
        // TODO: Implement REST call
        // POST /api/v1/pick-tasks
        return "";
    }

    @Override
    public void cancelPickTask(String pickTaskKey, String reason) {
        // TODO: Implement REST call
        // DELETE /api/v1/pick-tasks/{pickTaskKey}
    }

    @Override
    public void confirmPick(String pickTaskKey, InventoryKey inventoryKey, Quantity pickedQuantity) {
        // TODO: Implement REST call
        // POST /api/v1/pick-tasks/{pickTaskKey}/confirm
    }

    // ═══════════════════════════════════════════════════════════════
    // SHORT PICK HANDLING
    // ═══════════════════════════════════════════════════════════════

    @Override
    public void reportShortPick(String pickTaskKey, InventoryKey inventoryKey,
                                 Quantity expectedQuantity, Quantity actualQuantity, String reason) {
        // TODO: Implement REST call
        // POST /api/v1/pick-tasks/{pickTaskKey}/short-pick
    }

    @Override
    public String requestRepick(String originalPickTaskKey, Quantity shortQuantity) {
        // TODO: Implement REST call
        // POST /api/v1/pick-tasks/{originalPickTaskKey}/repick
        return "";
    }

    // ═══════════════════════════════════════════════════════════════
    // PICK CONFIRMATION CALLBACKS
    // ═══════════════════════════════════════════════════════════════

    @Override
    public void notifyInventoryPicked(String pickTaskKey, InventoryKey inventoryKey, Quantity quantity) {
        // TODO: Implement REST call or event publish
        // POST /api/v1/pick-tasks/{pickTaskKey}/inventory-picked
        // OR publish InventoryPickedEvent to Kafka
    }

    @Override
    public void notifyAllocationDeallocated(AllocationKey allocationKey, String reason) {
        // TODO: Implement REST call or event publish
        // POST /api/v1/allocations/{allocationKey}/deallocated
        // OR publish AllocationDeallocatedEvent to Kafka
    }
}
