package com.maersk.wms.outbound.service;

import com.maersk.wms.outbound.domain.Order;
import com.maersk.wms.outbound.domain.Wave;
import com.maersk.wms.outbound.domain.WaveStatus;
import com.maersk.wms.outbound.domain.repository.OrderRepository;
import com.maersk.wms.outbound.domain.repository.WaveRepository;
import com.maersk.wms.outbound.plugin.OutboundPluginContext;
import com.maersk.wms.outbound.plugin.OutboundPluginRegistry;
import com.maersk.wms.outbound.plugin.WavePlugin;
import com.maersk.wms.outbound.plugin.PluginResult;
import com.maersk.wms.outbound.rules.OutboundRulesEngine;
import com.maersk.wms.outbound.rules.WaveRuleFacts;
import com.maersk.wms.outbound.rules.WaveRuleResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for wave planning and management operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WaveService {

    private final WaveRepository waveRepository;
    private final OrderRepository orderRepository;
    private final OutboundPluginRegistry pluginRegistry;
    private final OutboundRulesEngine rulesEngine;

    /**
     * Create a wave from selected orders.
     */
    @Transactional
    public Wave createWave(List<String> orderNumbers, String waveType, OutboundPluginContext context) {
        log.info("Creating wave with {} orders", orderNumbers.size());

        // Get orders
        List<Order> orders = new ArrayList<>();
        for (String orderNumber : orderNumbers) {
            orderRepository.findByOrderNumber(orderNumber)
                    .ifPresent(orders::add);
        }

        if (orders.isEmpty()) {
            throw new OutboundOperationException("No valid orders found for wave");
        }

        // Filter orders through plugin
        Optional<WavePlugin> wavePlugin = pluginRegistry.getPlugin(WavePlugin.class, context);
        List<Order> filteredOrders = wavePlugin
                .map(p -> p.filterOrdersForWave(orders, context))
                .orElse(orders);

        // Create wave
        Wave wave = new Wave();
        wave.setWaveType(waveType);
        wave.setStatus(WaveStatus.NEW);
        wave.setOrderCount(filteredOrders.size());
        wave.setLineCount(filteredOrders.stream()
                .mapToInt(o -> o.getDetails().size())
                .sum());
        wave.setTotalUnits(filteredOrders.stream()
                .flatMap(o -> o.getDetails().stream())
                .mapToInt(d -> d.getOrderedQty().intValue())
                .sum());
        wave.setCreatedBy(context.getUserId());
        wave.setCreatedAt(LocalDateTime.now());

        // Execute before create plugins
        PluginResult beforeResult = pluginRegistry.executeAll(
                WavePlugin.class,
                context,
                plugin -> plugin.beforeWaveCreate(wave, filteredOrders, context)
        );

        if (!beforeResult.isSuccess()) {
            throw new OutboundOperationException("Wave creation blocked: " + beforeResult.getErrorMessage());
        }

        // Save wave
        Wave savedWave = waveRepository.save(wave);

        // Execute after create plugins
        pluginRegistry.executeAll(
                WavePlugin.class,
                context,
                plugin -> plugin.afterWaveCreate(savedWave, context)
        );

        log.info("Wave created: {} with {} orders, {} lines",
                savedWave.getWaveNumber(), savedWave.getOrderCount(), savedWave.getLineCount());

        return savedWave;
    }

    /**
     * Release a wave for processing.
     */
    @Transactional
    public Wave releaseWave(String waveNumber, OutboundPluginContext context) {
        log.info("Releasing wave: {}", waveNumber);

        Wave wave = waveRepository.findByWaveNumber(waveNumber)
                .orElseThrow(() -> new OutboundOperationException("Wave not found: " + waveNumber));

        if (wave.getStatus() != WaveStatus.NEW && wave.getStatus() != WaveStatus.PLANNED) {
            throw new OutboundOperationException(
                    "Wave must be NEW or PLANNED to release. Current status: " + wave.getStatus());
        }

        // Execute before release plugins
        PluginResult beforeResult = pluginRegistry.executeAll(
                WavePlugin.class,
                context,
                plugin -> plugin.beforeWaveRelease(wave, context)
        );

        if (!beforeResult.isSuccess()) {
            throw new OutboundOperationException("Wave release blocked: " + beforeResult.getErrorMessage());
        }

        // Update status
        wave.setStatus(WaveStatus.RELEASED);
        wave.setReleasedAt(LocalDateTime.now());
        wave.setReleasedBy(context.getUserId());

        Wave savedWave = waveRepository.save(wave);

        // Execute after release plugins
        pluginRegistry.executeAll(
                WavePlugin.class,
                context,
                plugin -> plugin.afterWaveRelease(savedWave, context)
        );

        log.info("Wave released: {}", waveNumber);

        return savedWave;
    }

    /**
     * Plan wave groupings using rules engine.
     */
    public WaveRuleResult planWaveGroupings(List<Order> orders, OutboundPluginContext context) {
        WaveRuleFacts facts = WaveRuleFacts.builder()
                .clientCode(context.getClientCode())
                .facilityCode(context.getFacilityCode())
                .orders(orders.stream()
                        .map(this::toWaveOrderFact)
                        .collect(Collectors.toList()))
                .clientConfig(context.getParameters())
                .build();

        return rulesEngine.evaluateWaveRules(facts);
    }

    /**
     * Get wave by number.
     */
    public Optional<Wave> getWave(String waveNumber) {
        return waveRepository.findByWaveNumber(waveNumber);
    }

    /**
     * Get waves by status.
     */
    public List<Wave> getWavesByStatus(WaveStatus status) {
        return waveRepository.findByStatus(status);
    }

    private WaveRuleFacts.WaveOrderFact toWaveOrderFact(Order order) {
        return WaveRuleFacts.WaveOrderFact.builder()
                .orderNumber(order.getOrderNumber())
                .orderType(order.getOrderType() != null ? order.getOrderType().name() : null)
                .customerCode(order.getCustomerCode())
                .carrier(order.getCarrierCode())
                .shipMethod(order.getShipMethod())
                .shipToCountry(order.getShipToCountry())
                .shipToState(order.getShipToState())
                .shipToZip(order.getShipToZip())
                .priority(order.getPriority() != null ? order.getPriority().ordinal() : 2)
                .lineCount(order.getDetails().size())
                .totalUnits(order.getDetails().stream()
                        .mapToInt(d -> d.getOrderedQty().intValue())
                        .sum())
                .requiredDate(order.getRequiredDate())
                .build();
    }
}
