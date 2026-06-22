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

import java.math.BigDecimal;
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
    public Wave createWave(List<String> orderKeys, String waveType, OutboundPluginContext context) {
        log.info("Creating wave with {} orders", orderKeys.size());

        // Get orders
        List<Order> orders = new ArrayList<>();
        for (String orderKey : orderKeys) {
            orderRepository.findByKey(orderKey)
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

        // Calculate totals
        int totalLines = filteredOrders.stream()
                .mapToInt(o -> o.getDetails() != null ? o.getDetails().size() : 0)
                .sum();
        BigDecimal totalQty = filteredOrders.stream()
                .flatMap(o -> o.getDetails() != null ? o.getDetails().stream() : java.util.stream.Stream.empty())
                .map(d -> d.getQtyOrdered() != null ? d.getQtyOrdered() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create wave
        Wave wave = Wave.builder()
                .waveType(waveType)
                .storerKey(context.getClientCode())
                .status(WaveStatus.PLANNED)
                .totalOrders(filteredOrders.size())
                .totalLines(totalLines)
                .totalQty(totalQty)
                .createdBy(context.getUserId())
                .addWho(context.getUserId())
                .addDate(LocalDateTime.now())
                .orderKeys(orderKeys)
                .build();

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
                savedWave.getWaveKey(), savedWave.getTotalOrders(), savedWave.getTotalLines());

        return savedWave;
    }

    /**
     * Release a wave for processing.
     */
    @Transactional
    public Wave releaseWave(String waveKey, OutboundPluginContext context) {
        log.info("Releasing wave: {}", waveKey);

        Wave wave = waveRepository.findByKey(waveKey)
                .orElseThrow(() -> new OutboundOperationException("Wave not found: " + waveKey));

        if (wave.getStatus() != WaveStatus.PLANNED) {
            throw new OutboundOperationException(
                    "Wave must be PLANNED to release. Current status: " + wave.getStatus());
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
        wave.setReleasedBy(context.getUserId());
        wave.setEditWho(context.getUserId());
        wave.setEditDate(LocalDateTime.now());

        Wave savedWave = waveRepository.save(wave);

        // Execute after release plugins
        pluginRegistry.executeAll(
                WavePlugin.class,
                context,
                plugin -> plugin.afterWaveRelease(savedWave, context)
        );

        log.info("Wave released: {}", waveKey);

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
     * Get wave by key.
     */
    public Optional<Wave> getWave(String waveKey) {
        return waveRepository.findByKey(waveKey);
    }

    /**
     * Get waves by status.
     */
    public List<Wave> getWavesByStatus(WaveStatus status) {
        return waveRepository.findByStatus(status);
    }

    private WaveRuleFacts.WaveOrderFact toWaveOrderFact(Order order) {
        int lineCount = order.getDetails() != null ? order.getDetails().size() : 0;
        int totalUnits = order.getDetails() != null
                ? order.getDetails().stream()
                        .filter(d -> d.getQtyOrdered() != null)
                        .mapToInt(d -> d.getQtyOrdered().intValue())
                        .sum()
                : 0;

        return WaveRuleFacts.WaveOrderFact.builder()
                .orderNumber(order.getOrderKey())
                .orderType(order.getOrderType())
                .customerCode(order.getConsigneeKey())
                .carrier(order.getCarrierCode())
                .shipMethod(order.getServiceLevel())
                .shipToCountry(order.getShipToCountry())
                .shipToState(order.getShipToState())
                .shipToZip(order.getShipToZip())
                .priority(order.getPriority() != null ? order.getPriority().ordinal() : 2)
                .lineCount(lineCount)
                .totalUnits(totalUnits)
                .requiredDate(order.getRequiredDeliveryDate())
                .build();
    }
}
