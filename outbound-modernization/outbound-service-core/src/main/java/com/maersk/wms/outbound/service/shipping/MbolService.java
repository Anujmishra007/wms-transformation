package com.maersk.wms.outbound.service.shipping;

import com.maersk.wms.outbound.domain.Order;
import com.maersk.wms.outbound.domain.Wave;
import com.maersk.wms.outbound.domain.shipping.*;
import com.maersk.wms.outbound.domain.shipping.dto.MbolValidationResult;
import com.maersk.wms.outbound.domain.repository.MbolRepository;
import com.maersk.wms.outbound.domain.repository.OrderRepository;
import com.maersk.wms.outbound.domain.repository.WaveRepository;
import com.maersk.wms.outbound.plugin.OutboundPluginContext;
import com.maersk.wms.outbound.plugin.OutboundPluginRegistry;
import com.maersk.wms.outbound.plugin.PluginResult;
import com.maersk.wms.outbound.plugin.shipping.MbolPlugin;
import com.maersk.wms.outbound.service.OutboundOperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for Master Bill of Lading (MBOL) operations.
 *
 * Legacy SP References:
 * - WM.lsp_WaveGenMBOL - Generate MBOL from wave
 * - WM.lsp_MBOLPPLLoadPlan_Wrapper - Populate MBOL from load plan
 * - WM.lsp_MBOLPPLOrderType2_Wrapper - Populate MBOL by order type
 * - WM.lsp_MBOLReleaseMoveTask_Wrapper - Release move tasks
 * - WM.lsp_WaveMoveOrderToNewMBOL - Move order to new MBOL
 * - nsp_BackEndValidateMBOL - Validate MBOL
 * - nsp_BackEndShipped - Mark MBOL as shipped
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MbolService {

    private final MbolRepository mbolRepository;
    private final OrderRepository orderRepository;
    private final WaveRepository waveRepository;
    private final OutboundPluginRegistry pluginRegistry;
    private final CarrierManagementService carrierService;

    /**
     * Generate MBOL from wave.
     *
     * Legacy Reference: WM.lsp_WaveGenMBOL
     */
    @Transactional
    public MasterBillOfLading generateFromWave(String waveKey, OutboundPluginContext context) {
        log.info("Generating MBOL from wave: {}", waveKey);

        Wave wave = waveRepository.findByWaveKey(waveKey)
                .orElseThrow(() -> new OutboundOperationException("Wave not found: " + waveKey));

        // Execute before MBOL generation plugins
        PluginResult beforeResult = pluginRegistry.executeAll(
                MbolPlugin.class,
                context,
                plugin -> plugin.beforeMbolGeneration(wave, context)
        );

        if (!beforeResult.isSuccess()) {
            throw new OutboundOperationException("MBOL generation blocked: " + beforeResult.getErrorMessage());
        }

        // Build MBOL from wave orders
        MasterBillOfLading mbol = MasterBillOfLading.builder()
                .waveKey(waveKey)
                .storerKey(wave.getStorerKey())
                .status(MbolStatus.NEW)
                .carrierCode(wave.getCarrierCode())
                .carrierServiceCode(wave.getServiceCode())
                .door(wave.getDoor())
                .route(wave.getRoute())
                .expectedShipDate(wave.getExpectedShipDate())
                .totalOrders(wave.getTotalOrders())
                .addWho(context.getUserId())
                .addDate(LocalDateTime.now())
                .build();

        // Calculate totals from orders
        calculateMbolTotals(mbol, wave.getOrderKeys());

        // Set ship-to from first order (for LTL/FTL shipments)
        if (!wave.getOrderKeys().isEmpty()) {
            orderRepository.findByOrderKey(wave.getOrderKeys().get(0))
                    .ifPresent(order -> {
                        mbol.setShipToName(order.getShipToName());
                        mbol.setShipToAddress1(order.getShipToAddress1());
                        mbol.setShipToAddress2(order.getShipToAddress2());
                        mbol.setShipToCity(order.getShipToCity());
                        mbol.setShipToState(order.getShipToState());
                        mbol.setShipToZip(order.getShipToZip());
                        mbol.setShipToCountry(order.getShipToCountry());
                    });
        }

        MasterBillOfLading savedMbol = mbolRepository.save(mbol);

        // Execute after MBOL generation plugins
        pluginRegistry.executeAll(
                MbolPlugin.class,
                context,
                plugin -> plugin.afterMbolGeneration(savedMbol, context)
        );

        log.info("MBOL generated: {} for wave: {}", savedMbol.getMbolKey(), waveKey);

        return savedMbol;
    }

    /**
     * Populate MBOL from load plan.
     *
     * Legacy Reference: WM.lsp_MBOLPPLLoadPlan_Wrapper
     */
    @Transactional
    public MasterBillOfLading populateFromLoadPlan(String loadKey, OutboundPluginContext context) {
        log.info("Populating MBOL from load plan: {}", loadKey);

        List<MasterBillOfLading> existingMbols = mbolRepository.findByLoadKey(loadKey);

        MasterBillOfLading mbol;
        if (existingMbols.isEmpty()) {
            mbol = MasterBillOfLading.builder()
                    .loadKey(loadKey)
                    .status(MbolStatus.NEW)
                    .addWho(context.getUserId())
                    .addDate(LocalDateTime.now())
                    .build();
        } else {
            mbol = existingMbols.get(0);
        }

        // Plugin hook for load plan population
        Optional<MbolPlugin> plugin = pluginRegistry.getPlugin(MbolPlugin.class, context);
        if (plugin.isPresent()) {
            plugin.get().populateFromLoadPlan(mbol, loadKey, context);
        }

        return mbolRepository.save(mbol);
    }

    /**
     * Validate MBOL before shipping.
     *
     * Legacy Reference: nsp_BackEndValidateMBOL
     */
    public MbolValidationResult validateMbol(String mbolKey, OutboundPluginContext context) {
        log.info("Validating MBOL: {}", mbolKey);

        MasterBillOfLading mbol = mbolRepository.findByKey(mbolKey)
                .orElseThrow(() -> new OutboundOperationException("MBOL not found: " + mbolKey));

        MbolValidationResult.MbolValidationResultBuilder resultBuilder = MbolValidationResult.builder()
                .mbolKey(mbolKey)
                .valid(true);

        // Basic validations
        if (mbol.getCarrierCode() == null || mbol.getCarrierCode().isEmpty()) {
            resultBuilder.valid(false);
            resultBuilder.error("Carrier not assigned");
        }

        if (mbol.getOrderKeys().isEmpty() && mbol.getCbols().isEmpty()) {
            resultBuilder.valid(false);
            resultBuilder.error("MBOL has no orders or packages");
        }

        if (mbol.getShipToAddress1() == null || mbol.getShipToAddress1().isEmpty()) {
            resultBuilder.valid(false);
            resultBuilder.error("Ship-to address missing");
        }

        // Plugin validation
        Optional<MbolPlugin> plugin = pluginRegistry.getPlugin(MbolPlugin.class, context);
        if (plugin.isPresent()) {
            MbolValidationResult pluginResult = plugin.get().validateMbol(mbol, context);
            if (!pluginResult.isValid()) {
                return pluginResult;
            }
        }

        return resultBuilder.build();
    }

    /**
     * Ship MBOL (mark as shipped).
     *
     * Legacy Reference: nsp_BackEndShipped
     */
    @Transactional
    public MasterBillOfLading shipMbol(String mbolKey, ShipConfirmRequest request, OutboundPluginContext context) {
        log.info("Shipping MBOL: {}", mbolKey);

        MasterBillOfLading mbol = mbolRepository.findByKey(mbolKey)
                .orElseThrow(() -> new OutboundOperationException("MBOL not found: " + mbolKey));

        // Validate before shipping
        MbolValidationResult validation = validateMbol(mbolKey, context);
        if (!validation.isValid()) {
            throw new OutboundOperationException("MBOL validation failed: " + validation.getErrors());
        }

        // Execute before ship plugins
        PluginResult beforeResult = pluginRegistry.executeAll(
                MbolPlugin.class,
                context,
                plugin -> plugin.beforeMbolShip(mbol, context)
        );

        if (!beforeResult.isSuccess()) {
            throw new OutboundOperationException("Ship blocked: " + beforeResult.getErrorMessage());
        }

        // Update MBOL
        mbol.setStatus(MbolStatus.SHIPPED);
        mbol.setActualShipDate(LocalDateTime.now());
        mbol.setTrackingNumber(request.getTrackingNumber());
        mbol.setProNumber(request.getProNumber());
        mbol.setTrailerNumber(request.getTrailerNumber());
        mbol.setSealNumber(request.getSealNumber());
        mbol.setEditWho(context.getUserId());
        mbol.setEditDate(LocalDateTime.now());

        MasterBillOfLading shippedMbol = mbolRepository.save(mbol);

        // Execute after ship plugins
        pluginRegistry.executeAll(
                MbolPlugin.class,
                context,
                plugin -> plugin.afterMbolShip(shippedMbol, context)
        );

        log.info("MBOL shipped: {} tracking: {}", mbolKey, request.getTrackingNumber());

        return shippedMbol;
    }

    /**
     * Move order to a new MBOL.
     *
     * Legacy Reference: WM.lsp_WaveMoveOrderToNewMBOL
     */
    @Transactional
    public MasterBillOfLading moveOrderToNewMbol(String orderKey, String sourceMbolKey, OutboundPluginContext context) {
        log.info("Moving order {} from MBOL {}", orderKey, sourceMbolKey);

        MasterBillOfLading sourceMbol = mbolRepository.findByKey(sourceMbolKey)
                .orElseThrow(() -> new OutboundOperationException("Source MBOL not found: " + sourceMbolKey));

        if (!sourceMbol.getOrderKeys().contains(orderKey)) {
            throw new OutboundOperationException("Order not in source MBOL");
        }

        Order order = orderRepository.findByOrderKey(orderKey)
                .orElseThrow(() -> new OutboundOperationException("Order not found: " + orderKey));

        // Create new MBOL
        MasterBillOfLading newMbol = MasterBillOfLading.builder()
                .storerKey(sourceMbol.getStorerKey())
                .waveKey(sourceMbol.getWaveKey())
                .status(MbolStatus.NEW)
                .carrierCode(sourceMbol.getCarrierCode())
                .carrierServiceCode(sourceMbol.getCarrierServiceCode())
                .shipToName(order.getShipToName())
                .shipToAddress1(order.getShipToAddress1())
                .shipToCity(order.getShipToCity())
                .shipToState(order.getShipToState())
                .shipToZip(order.getShipToZip())
                .shipToCountry(order.getShipToCountry())
                .addWho(context.getUserId())
                .addDate(LocalDateTime.now())
                .build();

        newMbol.getOrderKeys().add(orderKey);

        // Remove from source
        sourceMbol.getOrderKeys().remove(orderKey);
        mbolRepository.save(sourceMbol);

        return mbolRepository.save(newMbol);
    }

    /**
     * Get MBOL by key.
     */
    public Optional<MasterBillOfLading> getMbol(String mbolKey) {
        return mbolRepository.findByKey(mbolKey);
    }

    /**
     * Get MBOLs by status.
     */
    public List<MasterBillOfLading> getMbolsByStatus(MbolStatus status) {
        return mbolRepository.findByStatus(status);
    }

    private void calculateMbolTotals(MasterBillOfLading mbol, List<String> orderKeys) {
        BigDecimal totalWeight = BigDecimal.ZERO;
        int totalCartons = 0;
        int totalPallets = 0;

        for (String orderKey : orderKeys) {
            Optional<Order> orderOpt = orderRepository.findByOrderKey(orderKey);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                if (order.getTotalWeight() != null) {
                    totalWeight = totalWeight.add(order.getTotalWeight());
                }
                totalCartons += order.getTotalCartons();
                totalPallets += order.getTotalPallets();
            }
            mbol.getOrderKeys().add(orderKey);
        }

        mbol.setTotalWeight(totalWeight);
        mbol.setTotalCartons(totalCartons);
        mbol.setTotalPallets(totalPallets);
    }
}
