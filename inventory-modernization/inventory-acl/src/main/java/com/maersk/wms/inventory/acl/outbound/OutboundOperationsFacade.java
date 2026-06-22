package com.maersk.wms.inventory.acl.outbound;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Anti-Corruption Layer facade for Outbound Operations Service.
 * Translates outbound domain concepts to inventory domain.
 * Downstream service for order fulfillment and shipping.
 */
public interface OutboundOperationsFacade {

    // ═══════════════════════════════════════════════════════════════
    // ORDER QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Get order details for allocation.
     */
    Optional<OrderInfo> getOrderInfo(OrderKey orderKey);

    /**
     * Get order line details.
     */
    Optional<OrderLineInfo> getOrderLineInfo(OrderKey orderKey, String lineNumber);

    /**
     * Get orders ready for allocation.
     */
    List<OrderInfo> getOrdersReadyForAllocation(WarehouseKey warehouseKey);

    /**
     * Get order lines pending allocation.
     */
    List<OrderLineInfo> getOrderLinesPendingAllocation(OrderKey orderKey);

    // ═══════════════════════════════════════════════════════════════
    // ALLOCATION INTEGRATION
    // ═══════════════════════════════════════════════════════════════

    /**
     * Request allocation for order.
     */
    AllocationKey requestAllocation(OrderKey orderKey, String lineNumber, AllocationCriteria criteria);

    /**
     * Notify order of successful allocation.
     */
    void notifyAllocationComplete(OrderKey orderKey, String lineNumber, AllocationResult result);

    /**
     * Notify order of allocation shortage.
     */
    void notifyAllocationShortage(OrderKey orderKey, String lineNumber,
                                   Quantity requestedQuantity, Quantity allocatedQuantity);

    /**
     * Notify order of deallocation.
     */
    void notifyDeallocation(OrderKey orderKey, String lineNumber, String reason);

    // ═══════════════════════════════════════════════════════════════
    // SHIPMENT QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Get shipment details.
     */
    Optional<ShipmentInfo> getShipmentInfo(String shipmentKey);

    /**
     * Get shipments for order.
     */
    List<ShipmentInfo> getShipmentsForOrder(OrderKey orderKey);

    /**
     * Get inventory on shipment.
     */
    List<InventoryKey> getInventoryOnShipment(String shipmentKey);

    // ═══════════════════════════════════════════════════════════════
    // SHIPPING CONFIRMATION
    // ═══════════════════════════════════════════════════════════════

    /**
     * Confirm shipment (triggers inventory removal).
     */
    void confirmShipment(String shipmentKey, UserKey confirmedBy);

    /**
     * Notify outbound of inventory shipped.
     */
    void notifyInventoryShipped(String shipmentKey, List<InventoryKey> inventoryKeys);

    // ═══════════════════════════════════════════════════════════════
    // DTOs
    // ═══════════════════════════════════════════════════════════════

    record OrderInfo(
            OrderKey orderKey,
            String orderNumber,
            StorerKey storerKey,
            WarehouseKey warehouseKey,
            String orderType,
            String status,
            Instant orderDate,
            Instant requiredDate,
            int priority,
            String externalReference,
            int totalLines
    ) {}

    record OrderLineInfo(
            OrderKey orderKey,
            String lineNumber,
            SkuKey skuKey,
            Quantity orderedQuantity,
            Quantity allocatedQuantity,
            Quantity pickedQuantity,
            Quantity shippedQuantity,
            String status,
            LottableAttributes requiredLottables,
            boolean[] lottableMatchFlags
    ) {}

    record ShipmentInfo(
            String shipmentKey,
            OrderKey orderKey,
            StorerKey storerKey,
            WarehouseKey warehouseKey,
            String shipmentType,
            String status,
            String carrier,
            String trackingNumber,
            Instant shipDate,
            int totalCartons
    ) {}
}
