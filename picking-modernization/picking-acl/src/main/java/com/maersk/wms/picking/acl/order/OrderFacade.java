package com.maersk.wms.picking.acl.order;

import com.maersk.wms.picking.shared.kernel.identifiers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Anti-Corruption Layer facade for Order Service integration.
 * Handles order status updates and order information queries.
 */
public interface OrderFacade {

    // Order Status
    void updateOrderPickStatus(OrderKey orderKey, String pickStatus);
    void markOrderPickComplete(OrderKey orderKey);
    void markOrderPickPartial(OrderKey orderKey, int pickedLines, int totalLines);
    void markOrderShorted(OrderKey orderKey, String shortReason);

    // Order Line Updates
    void updateOrderLineStatus(PickDetailKey pickDetailKey, String status);
    void updateOrderLinePickedQty(PickDetailKey pickDetailKey, java.math.BigDecimal pickedQty);

    // Query
    Optional<OrderInfo> getOrderInfo(OrderKey orderKey);
    List<OrderInfo> getOrdersByWave(WaveKey waveKey);
    Optional<OrderLineInfo> getOrderLineInfo(PickDetailKey pickDetailKey);
    int getOpenPickCountForOrder(OrderKey orderKey);

    // Priority
    int getOrderPriority(OrderKey orderKey);
    LocalDateTime getOrderShipDate(OrderKey orderKey);
    boolean isOrderRush(OrderKey orderKey);

    // Cancellation
    void notifyOrderCancelled(OrderKey orderKey, String reason);
    void notifyLineCancelled(PickDetailKey pickDetailKey, String reason);

    /**
     * Order information DTO.
     */
    record OrderInfo(
            OrderKey orderKey,
            WaveKey waveKey,
            String orderType,
            String orderStatus,
            String pickStatus,
            int priority,
            LocalDateTime shipDate,
            String carrier,
            String route,
            int totalLines,
            int pickedLines,
            int shortedLines,
            String customerRef
    ) {}

    /**
     * Order line information DTO.
     */
    record OrderLineInfo(
            PickDetailKey pickDetailKey,
            OrderKey orderKey,
            SkuKey sku,
            java.math.BigDecimal orderedQty,
            java.math.BigDecimal allocatedQty,
            java.math.BigDecimal pickedQty,
            java.math.BigDecimal shortedQty,
            String lineStatus
    ) {}
}
