package com.maersk.wms.inventory.acl.outbound;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Adapter implementation for Outbound Operations Service facade.
 * Communicates with outbound-operations-service via REST/gRPC.
 */
@Component
public class OutboundOperationsAdapter implements OutboundOperationsFacade {

    private final WebClient outboundClient;

    public OutboundOperationsAdapter(WebClient.Builder webClientBuilder) {
        this.outboundClient = webClientBuilder
                .baseUrl("${services.outbound-operations.url:http://outbound-operations-service}")
                .build();
    }

    // ═══════════════════════════════════════════════════════════════
    // ORDER QUERIES
    // ═══════════════════════════════════════════════════════════════

    @Override
    public Optional<OrderInfo> getOrderInfo(OrderKey orderKey) {
        // TODO: Implement REST call to outbound-operations-service
        // GET /api/v1/orders/{orderKey}
        return Optional.empty();
    }

    @Override
    public Optional<OrderLineInfo> getOrderLineInfo(OrderKey orderKey, String lineNumber) {
        // TODO: Implement REST call
        // GET /api/v1/orders/{orderKey}/lines/{lineNumber}
        return Optional.empty();
    }

    @Override
    public List<OrderInfo> getOrdersReadyForAllocation(WarehouseKey warehouseKey) {
        // TODO: Implement REST call
        // GET /api/v1/orders?warehouse={warehouseKey}&status=READY_FOR_ALLOCATION
        return Collections.emptyList();
    }

    @Override
    public List<OrderLineInfo> getOrderLinesPendingAllocation(OrderKey orderKey) {
        // TODO: Implement REST call
        // GET /api/v1/orders/{orderKey}/lines?status=PENDING_ALLOCATION
        return Collections.emptyList();
    }

    // ═══════════════════════════════════════════════════════════════
    // ALLOCATION INTEGRATION
    // ═══════════════════════════════════════════════════════════════

    @Override
    public AllocationKey requestAllocation(OrderKey orderKey, String lineNumber, AllocationCriteria criteria) {
        // TODO: Implement REST call
        // POST /api/v1/orders/{orderKey}/lines/{lineNumber}/allocate
        return new AllocationKey("");
    }

    @Override
    public void notifyAllocationComplete(OrderKey orderKey, String lineNumber, AllocationResult result) {
        // TODO: Implement REST call or event publish
        // POST /api/v1/orders/{orderKey}/lines/{lineNumber}/allocation-complete
        // OR publish AllocationCompleteEvent to Kafka
    }

    @Override
    public void notifyAllocationShortage(OrderKey orderKey, String lineNumber,
                                          Quantity requestedQuantity, Quantity allocatedQuantity) {
        // TODO: Implement REST call or event publish
        // POST /api/v1/orders/{orderKey}/lines/{lineNumber}/allocation-shortage
        // OR publish AllocationShortageEvent to Kafka
    }

    @Override
    public void notifyDeallocation(OrderKey orderKey, String lineNumber, String reason) {
        // TODO: Implement REST call or event publish
        // POST /api/v1/orders/{orderKey}/lines/{lineNumber}/deallocated
        // OR publish DeallocationEvent to Kafka
    }

    // ═══════════════════════════════════════════════════════════════
    // SHIPMENT QUERIES
    // ═══════════════════════════════════════════════════════════════

    @Override
    public Optional<ShipmentInfo> getShipmentInfo(String shipmentKey) {
        // TODO: Implement REST call
        // GET /api/v1/shipments/{shipmentKey}
        return Optional.empty();
    }

    @Override
    public List<ShipmentInfo> getShipmentsForOrder(OrderKey orderKey) {
        // TODO: Implement REST call
        // GET /api/v1/orders/{orderKey}/shipments
        return Collections.emptyList();
    }

    @Override
    public List<InventoryKey> getInventoryOnShipment(String shipmentKey) {
        // TODO: Implement REST call
        // GET /api/v1/shipments/{shipmentKey}/inventory
        return Collections.emptyList();
    }

    // ═══════════════════════════════════════════════════════════════
    // SHIPPING CONFIRMATION
    // ═══════════════════════════════════════════════════════════════

    @Override
    public void confirmShipment(String shipmentKey, UserKey confirmedBy) {
        // TODO: Implement REST call
        // POST /api/v1/shipments/{shipmentKey}/confirm
    }

    @Override
    public void notifyInventoryShipped(String shipmentKey, List<InventoryKey> inventoryKeys) {
        // TODO: Implement REST call or event publish
        // POST /api/v1/shipments/{shipmentKey}/inventory-shipped
        // OR publish InventoryShippedEvent to Kafka
    }
}
