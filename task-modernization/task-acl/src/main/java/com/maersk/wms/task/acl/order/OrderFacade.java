package com.maersk.wms.task.acl.order;

import com.maersk.wms.task.shared.kernel.identifiers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Facade interface for Order Service integration.
 * Used by Task Management for order-related task context.
 */
public interface OrderFacade {

    /**
     * Get order details.
     */
    Optional<OrderDetails> getOrderDetails(OrderKey orderKey);

    /**
     * Get orders for a wave.
     */
    List<OrderDetails> getOrdersForWave(WaveKey waveKey);

    /**
     * Get order priority.
     */
    int getOrderPriority(OrderKey orderKey);

    /**
     * Get order SLA deadline.
     */
    Optional<LocalDateTime> getOrderSlaDeadline(OrderKey orderKey);

    /**
     * Notify order of task progress.
     */
    void notifyTaskStarted(String taskKey, OrderKey orderKey);
    void notifyTaskCompleted(String taskKey, OrderKey orderKey);

    /**
     * Record for order details.
     */
    record OrderDetails(
            OrderKey orderKey,
            String customerId,
            WaveKey waveKey,
            String carrierId,
            String shipmentKey,
            LocalDateTime dueDate,
            int priority,
            String status
    ) {}
}
