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
    public Shipment createShipment(String orderKey, OutboundPluginContext context) {
        log.info("Creating shipment for order: {}", orderKey);

        Order order = orderRepository.findByKey(orderKey)
                .orElseThrow(() -> new OutboundOperationException("Order not found: " + orderKey));

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

        // Generate shipment key
        String shipmentKey = shipmentRepository.generateShipmentKey();

        // Create shipment
        Shipment shipment = Shipment.builder()
                .shipmentKey(shipmentKey)
                .storerKey(order.getStorerKey())
                .carrierCode(carrier)
                .carrierName(order.getCarrierName())
                .serviceLevel(order.getServiceLevel())
                .type(ShipmentType.PARCEL)
                .status(ShipmentStatus.NEW)
                .shipToName(order.getConsigneeName())
                .shipToAddress1(order.getShipToAddress1())
                .shipToAddress2(order.getShipToAddress2())
                .shipToCity(order.getShipToCity())
                .shipToState(order.getShipToState())
                .shipToZip(order.getShipToZip())
                .shipToCountry(order.getShipToCountry())
                .addWho(context.getUserId())
                .addDate(LocalDateTime.now())
                .orderKeys(List.of(orderKey))
                .build();

        Shipment savedShipment = shipmentRepository.save(shipment);

        // Execute after shipment create plugins
        pluginRegistry.executeAll(
                ShippingPlugin.class,
                context,
                plugin -> plugin.afterShipmentCreate(savedShipment, context)
        );

        log.info("Shipment created: {} for order: {}", savedShipment.getShipmentKey(), orderKey);

        return savedShipment;
    }

    /**
     * Confirm shipment (ship confirm).
     */
    @Transactional
    public Shipment confirmShipment(String shipmentKey, OutboundPluginContext context) {
        log.info("Confirming shipment: {}", shipmentKey);

        Shipment shipment = shipmentRepository.findByKey(shipmentKey)
                .orElseThrow(() -> new OutboundOperationException("Shipment not found: " + shipmentKey));

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
        shipment.setActualShipDate(LocalDateTime.now());
        shipment.setEditWho(context.getUserId());
        shipment.setEditDate(LocalDateTime.now());

        Shipment savedShipment = shipmentRepository.save(shipment);

        // Execute after ship confirm plugins
        pluginRegistry.executeAll(
                ShippingPlugin.class,
                context,
                plugin -> plugin.afterShipConfirm(savedShipment, context)
        );

        log.info("Shipment confirmed: {} with tracking: {}",
                savedShipment.getShipmentKey(), savedShipment.getTrackingNumber());

        return savedShipment;
    }

    /**
     * Generate manifest for shipment.
     */
    @Transactional
    public Shipment generateManifest(String shipmentKey, OutboundPluginContext context) {
        log.info("Generating manifest for shipment: {}", shipmentKey);

        Shipment shipment = shipmentRepository.findByKey(shipmentKey)
                .orElseThrow(() -> new OutboundOperationException("Shipment not found: " + shipmentKey));

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
        shipment.setEditDate(LocalDateTime.now());
        shipment.setEditWho(context.getUserId());

        Shipment savedShipment = shipmentRepository.save(shipment);

        // Execute after manifest plugins
        pluginRegistry.executeAll(
                ShippingPlugin.class,
                context,
                plugin -> plugin.afterManifest(savedShipment, context)
        );

        log.info("Manifest generated for shipment: {}", shipmentKey);

        return savedShipment;
    }

    /**
     * Get shipment by key.
     */
    public Optional<Shipment> getShipment(String shipmentKey) {
        return shipmentRepository.findByKey(shipmentKey);
    }

    /**
     * Get shipments by status.
     */
    public List<Shipment> getShipmentsByStatus(ShipmentStatus status) {
        return shipmentRepository.findByStatus(status);
    }

    private ShippingRuleFacts buildShippingFacts(Shipment shipment, OutboundPluginContext context) {
        String orderKey = shipment.getOrderKeys() != null && !shipment.getOrderKeys().isEmpty()
                ? shipment.getOrderKeys().get(0)
                : null;

        return ShippingRuleFacts.builder()
                .clientCode(context.getClientCode())
                .facilityCode(context.getFacilityCode())
                .orderNumber(orderKey)
                .shipmentId(shipment.getShipmentKey())
                .carrier(shipment.getCarrierCode())
                .shipMethod(shipment.getServiceLevel())
                .shipToCountry(shipment.getShipToCountry())
                .shipToState(shipment.getShipToState())
                .shipToZip(shipment.getShipToZip())
                .totalWeight(shipment.getTotalWeight())
                .clientConfig(context.getParameters())
                .build();
    }
}
