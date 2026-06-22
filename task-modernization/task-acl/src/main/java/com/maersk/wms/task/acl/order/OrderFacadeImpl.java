package com.maersk.wms.task.acl.order;

import com.maersk.wms.task.shared.kernel.identifiers.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of OrderFacade.
 * Placeholder for actual service integration.
 */
@Component
public class OrderFacadeImpl implements OrderFacade {

    @Override
    public Optional<OrderDetails> getOrderDetails(OrderKey orderKey) {
        // TODO: Connect to order service
        return Optional.empty();
    }

    @Override
    public List<OrderDetails> getOrdersForWave(WaveKey waveKey) {
        // TODO: Connect to order service
        return Collections.emptyList();
    }

    @Override
    public int getOrderPriority(OrderKey orderKey) {
        // TODO: Connect to order service
        return 50; // Default medium priority
    }

    @Override
    public Optional<LocalDateTime> getOrderSlaDeadline(OrderKey orderKey) {
        // TODO: Connect to order service
        return Optional.empty();
    }

    @Override
    public void notifyTaskStarted(String taskKey, OrderKey orderKey) {
        // TODO: Connect to order service - probably via event
    }

    @Override
    public void notifyTaskCompleted(String taskKey, OrderKey orderKey) {
        // TODO: Connect to order service - probably via event
    }
}
