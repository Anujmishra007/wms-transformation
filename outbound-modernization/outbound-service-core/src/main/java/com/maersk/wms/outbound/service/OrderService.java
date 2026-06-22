package com.maersk.wms.outbound.service;

import com.maersk.wms.outbound.domain.Order;
import com.maersk.wms.outbound.domain.OrderDetail;
import com.maersk.wms.outbound.domain.OrderDetailStatus;
import com.maersk.wms.outbound.domain.OrderStatus;
import com.maersk.wms.outbound.domain.repository.OrderRepository;
import com.maersk.wms.outbound.plugin.OutboundPluginContext;
import com.maersk.wms.outbound.plugin.OutboundPluginRegistry;
import com.maersk.wms.outbound.plugin.AllocationPlugin;
import com.maersk.wms.outbound.plugin.PluginResult;
import com.maersk.wms.outbound.rules.OutboundRulesEngine;
import com.maersk.wms.outbound.rules.AllocationRuleFacts;
import com.maersk.wms.outbound.rules.AllocationRuleResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for order management operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboundPluginRegistry pluginRegistry;
    private final OutboundRulesEngine rulesEngine;

    /**
     * Create a new order.
     */
    @Transactional
    public Order createOrder(Order order, OutboundPluginContext context) {
        log.info("Creating order: {} for client: {}", order.getOrderKey(), context.getClientCode());

        // Validate order
        validateOrder(order);

        // Set initial status
        order.setStatus(OrderStatus.NEW);
        order.setAddWho(context.getUserId());
        order.setAddDate(LocalDateTime.now());

        // Set line statuses
        for (OrderDetail detail : order.getDetails()) {
            detail.setStatus(OrderDetailStatus.NEW);
        }

        // Save order
        Order savedOrder = orderRepository.save(order);

        log.info("Order created: {} with {} lines", savedOrder.getOrderKey(),
                savedOrder.getDetails().size());

        return savedOrder;
    }

    /**
     * Allocate inventory for an order.
     */
    @Transactional
    public AllocationResult allocateOrder(String orderKey, OutboundPluginContext context) {
        log.info("Allocating order: {}", orderKey);

        Order order = orderRepository.findByKey(orderKey)
                .orElseThrow(() -> new OutboundOperationException("Order not found: " + orderKey));

        // Execute before allocation plugins
        PluginResult beforeResult = pluginRegistry.executeAll(
                AllocationPlugin.class,
                context,
                plugin -> plugin.beforeAllocate(order, context)
        );

        if (!beforeResult.isSuccess()) {
            return AllocationResult.failed(beforeResult.getErrorMessage());
        }

        // Get allocation strategy from plugin
        Optional<AllocationPlugin> allocationPlugin = pluginRegistry.getPlugin(
                AllocationPlugin.class, context);

        String strategy = allocationPlugin
                .map(p -> p.determineStrategy(order, context).name())
                .orElse("FIFO");

        // Evaluate allocation rules
        AllocationRuleFacts facts = buildAllocationFacts(order, context);
        AllocationRuleResult ruleResult = rulesEngine.evaluateAllocationRules(facts);

        if (!ruleResult.isAllocationAllowed()) {
            return AllocationResult.failed(String.join(", ", ruleResult.getValidationErrors()));
        }

        // Perform allocation (simplified - actual implementation would create PICKDETAIL records)
        order.setStatus(OrderStatus.ALLOCATED);
        orderRepository.save(order);

        // Execute after allocation plugins
        pluginRegistry.executeAll(
                AllocationPlugin.class,
                context,
                plugin -> plugin.afterAllocate(order, List.of(), context)
        );

        log.info("Order {} allocated with strategy: {}", orderKey, strategy);

        return AllocationResult.success(order);
    }

    /**
     * Release order for picking.
     */
    @Transactional
    public Order releaseOrder(String orderKey, OutboundPluginContext context) {
        log.info("Releasing order: {}", orderKey);

        Order order = orderRepository.findByKey(orderKey)
                .orElseThrow(() -> new OutboundOperationException("Order not found: " + orderKey));

        if (order.getStatus() != OrderStatus.ALLOCATED) {
            throw new OutboundOperationException(
                    "Order must be allocated before release. Current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.RELEASED);
        order.setEditDate(LocalDateTime.now());
        order.setEditWho(context.getUserId());

        return orderRepository.save(order);
    }

    /**
     * Get order by key.
     */
    public Optional<Order> getOrder(String orderKey) {
        return orderRepository.findByKey(orderKey);
    }

    /**
     * Get orders by status.
     */
    public List<Order> getOrdersByStatus(OrderStatus status, String clientCode) {
        return orderRepository.findByStatus(status);
    }

    private void validateOrder(Order order) {
        if (order.getOrderKey() == null || order.getOrderKey().isEmpty()) {
            throw new OutboundOperationException("Order key is required");
        }
        if (order.getDetails() == null || order.getDetails().isEmpty()) {
            throw new OutboundOperationException("Order must have at least one line");
        }
    }

    private AllocationRuleFacts buildAllocationFacts(Order order, OutboundPluginContext context) {
        return AllocationRuleFacts.builder()
                .clientCode(context.getClientCode())
                .facilityCode(context.getFacilityCode())
                .orderNumber(order.getOrderKey())
                .orderType(order.getOrderType())
                .customerCode(order.getConsigneeKey())
                .priority(order.getPriority() != null ? order.getPriority().ordinal() : 2)
                .clientConfig(context.getParameters())
                .build();
    }

    /**
     * Result of allocation operation.
     */
    @lombok.Data
    @lombok.Builder
    public static class AllocationResult {
        private boolean success;
        private Order order;
        private String errorMessage;
        private List<String> allocationIds;

        public static AllocationResult success(Order order) {
            return AllocationResult.builder()
                    .success(true)
                    .order(order)
                    .build();
        }

        public static AllocationResult failed(String error) {
            return AllocationResult.builder()
                    .success(false)
                    .errorMessage(error)
                    .build();
        }
    }
}
