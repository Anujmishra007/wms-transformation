package com.maersk.wms.outbound.service;

import com.maersk.wms.outbound.domain.Order;
import com.maersk.wms.outbound.domain.Shipment;
import com.maersk.wms.outbound.domain.ShipmentStatus;
import com.maersk.wms.outbound.domain.ShipmentType;
import com.maersk.wms.outbound.domain.repository.OrderRepository;
import com.maersk.wms.outbound.domain.repository.ShipmentRepository;
import com.maersk.wms.outbound.plugin.OutboundPluginContext;
import com.maersk.wms.outbound.plugin.OutboundPluginRegistry;
import com.maersk.wms.outbound.plugin.ShippingPlugin;
import com.maersk.wms.outbound.plugin.PluginResult;
import com.maersk.wms.outbound.rules.OutboundRulesEngine;
import com.maersk.wms.outbound.rules.ShippingRuleFacts;
import com.maersk.wms.outbound.rules.ShippingRuleResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for shipment management operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final OrderRepository orderRepository;
    private final OutboundPluginRegistry pluginRegistry;
    private final OutboundRulesEngine rulesEngine;

    /**
     * Create a shipment for an order.
     */
    @Transactional
    public Shipment createShipment(String orderNumber, OutboundPluginContext context) {
        log.info("Creating shipment for order: {}", orderNumber);

        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OutboundOperationException("Order not found: " + orderNumber));

        // Execute before shipment create plugins
        PluginResult beforeResult = pluginRegistry.executeAll(
                ShippingPlugin.class,
                context,
                plugin -> plugin.beforeShipmentCreate(order, context)
        );

        if (!beforeResult.isSuccess()) {
            throw new OutboundOperationException("Shipment creation blocked: " + beforeResult.getErrorMessage());
        }

        // Select carrier through plugin
        Optional<ShippingPlugin> shippingPlugin = pluginRegistry.getPlugin(ShippingPlugin.class, context);
        String carrier = shippingPlugin
                .map(p -> p.selectCarrier(order, context))
                .orElse(order.getCarrierCode());

        // Create shipment
        Shipment shipment = new Shipment();
        shipment.setOrderNumber(orderNumber);
        shipment.setCarrierCode(carrier);
        shipment.setShipMethod(order.getShipMethod());
        shipment.setShipmentType(ShipmentType.PARCEL);
        shipment.setStatus(ShipmentStatus.NEW);
        shipment.setShipToName(order.getShipToName());
        shipment.setShipToAddress1(order.getShipToAddress1());
        shipment.setShipToAddress2(order.getShipToAddress2());
        shipment.setShipToCity(order.getShipToCity());
        shipment.setShipToState(order.getShipToState());
        shipment.setShipToZip(order.getShipToZip());
        shipment.setShipToCountry(order.getShipToCountry());
        shipment.setCreatedBy(context.getUserId());
        shipment.setCreatedAt(LocalDateTime.now());

        Shipment savedShipment = shipmentRepository.save(shipment);

        // Execute after shipment create plugins
        pluginRegistry.executeAll(
                ShippingPlugin.class,
                context,
                plugin -> plugin.afterShipmentCreate(savedShipment, context)
        );

        log.info("Shipment created: {} for order: {}", savedShipment.getShipmentId(), orderNumber);

        return savedShipment;
    }

    /**
     * Confirm shipment (ship confirm).
     */
    @Transactional
    public Shipment confirmShipment(String shipmentId, OutboundPluginContext context) {
        log.info("Confirming shipment: {}", shipmentId);

        Shipment shipment = shipmentRepository.findByShipmentId(shipmentId)
                .orElseThrow(() -> new OutboundOperationException("Shipment not found: " + shipmentId));

        // Execute before ship confirm plugins
        PluginResult beforeResult = pluginRegistry.executeAll(
                ShippingPlugin.class,
                context,
                plugin -> plugin.beforeShipConfirm(shipment, context)
        );

        if (!beforeResult.isSuccess()) {
            throw new OutboundOperationException("Ship confirm blocked: " + beforeResult.getErrorMessage());
        }

        // Calculate freight
        Optional<ShippingPlugin> shippingPlugin = pluginRegistry.getPlugin(ShippingPlugin.class, context);
        BigDecimal freightCharge = shippingPlugin
                .map(p -> p.calculateFreight(shipment, context))
                .orElse(BigDecimal.ZERO);

        // Update shipment
        shipment.setStatus(ShipmentStatus.SHIPPED);
        shipment.setFreightCharge(freightCharge);
        shipment.setShippedAt(LocalDateTime.now());
        shipment.setShippedBy(context.getUserId());

        Shipment savedShipment = shipmentRepository.save(shipment);

        // Execute after ship confirm plugins
        pluginRegistry.executeAll(
                ShippingPlugin.class,
                context,
                plugin -> plugin.afterShipConfirm(savedShipment, context)
        );

        log.info("Shipment confirmed: {} with tracking: {}",
                savedShipment.getShipmentId(), savedShipment.getTrackingNumber());

        return savedShipment;
    }

    /**
     * Generate manifest for shipment.
     */
    @Transactional
    public Shipment generateManifest(String shipmentId, OutboundPluginContext context) {
        log.info("Generating manifest for shipment: {}", shipmentId);

        Shipment shipment = shipmentRepository.findByShipmentId(shipmentId)
                .orElseThrow(() -> new OutboundOperationException("Shipment not found: " + shipmentId));

        // Execute before manifest plugins
        PluginResult beforeResult = pluginRegistry.executeAll(
                ShippingPlugin.class,
                context,
                plugin -> plugin.beforeManifest(shipment, context)
        );

        if (!beforeResult.isSuccess()) {
            throw new OutboundOperationException("Manifest generation blocked: " + beforeResult.getErrorMessage());
        }

        // Evaluate shipping rules
        ShippingRuleFacts facts = buildShippingFacts(shipment, context);
        ShippingRuleResult ruleResult = rulesEngine.evaluateShippingRules(facts);

        if (!ruleResult.isShippingAllowed()) {
            throw new OutboundOperationException(
                    "Shipping not allowed: " + String.join(", ", ruleResult.getValidationErrors()));
        }

        // Update shipment with manifest
        shipment.setStatus(ShipmentStatus.MANIFESTED);
        shipment.setManifestedAt(LocalDateTime.now());

        Shipment savedShipment = shipmentRepository.save(shipment);

        // Execute after manifest plugins
        pluginRegistry.executeAll(
                ShippingPlugin.class,
                context,
                plugin -> plugin.afterManifest(savedShipment, context)
        );

        log.info("Manifest generated for shipment: {}", shipmentId);

        return savedShipment;
    }

    /**
     * Get shipment by ID.
     */
    public Optional<Shipment> getShipment(String shipmentId) {
        return shipmentRepository.findByShipmentId(shipmentId);
    }

    /**
     * Get shipments by status.
     */
    public List<Shipment> getShipmentsByStatus(ShipmentStatus status) {
        return shipmentRepository.findByStatus(status);
    }

    private ShippingRuleFacts buildShippingFacts(Shipment shipment, OutboundPluginContext context) {
        return ShippingRuleFacts.builder()
                .clientCode(context.getClientCode())
                .facilityCode(context.getFacilityCode())
                .orderNumber(shipment.getOrderNumber())
                .shipmentId(shipment.getShipmentId())
                .carrier(shipment.getCarrierCode())
                .shipMethod(shipment.getShipMethod())
                .shipToCountry(shipment.getShipToCountry())
                .shipToState(shipment.getShipToState())
                .shipToZip(shipment.getShipToZip())
                .totalWeight(shipment.getTotalWeight())
                .clientConfig(context.getParameters())
                .build();
    }
}
