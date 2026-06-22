package com.maersk.wms.outbound.service.shipping;

import com.maersk.wms.outbound.domain.Order;
import com.maersk.wms.outbound.domain.shipping.*;
import com.maersk.wms.outbound.domain.repository.CarrierRepository;
import com.maersk.wms.outbound.domain.repository.MbolRepository;
import com.maersk.wms.outbound.plugin.OutboundPluginContext;
import com.maersk.wms.outbound.plugin.OutboundPluginRegistry;
import com.maersk.wms.outbound.plugin.shipping.CarrierSelectionPlugin;
import com.maersk.wms.outbound.service.OutboundOperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for carrier management operations.
 *
 * Legacy SP References:
 * - isp_ConnectCarrierService
 * - isp_UpdateCarrierService
 * - isp_ValidateCarrierChange
 * - isp_Carrier_Middleware_Interface
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CarrierManagementService {

    private final CarrierRepository carrierRepository;
    private final MbolRepository mbolRepository;
    private final OutboundPluginRegistry pluginRegistry;

    /**
     * Get carrier by code.
     */
    public Optional<Carrier> getCarrier(String carrierCode) {
        return carrierRepository.findByCode(carrierCode);
    }

    /**
     * Get all active carriers.
     */
    public List<Carrier> getActiveCarriers() {
        return carrierRepository.findActive();
    }

    /**
     * Get carriers available for a specific facility.
     */
    public List<Carrier> getCarriersForFacility(String facilityCode, OutboundPluginContext context) {
        List<Carrier> allCarriers = carrierRepository.findActive();

        // Apply plugin filtering if available
        Optional<CarrierSelectionPlugin> plugin = pluginRegistry.getPlugin(
                CarrierSelectionPlugin.class, context);

        if (plugin.isPresent()) {
            return plugin.get().filterAvailableCarriers(allCarriers, context);
        }

        return allCarriers;
    }

    /**
     * Select best carrier for an order based on rules and plugins.
     *
     * Legacy Reference: Carrier selection logic from StorerConfig and CODELKUP
     */
    @Transactional(readOnly = true)
    public CarrierSelectionResult selectCarrier(Order order, OutboundPluginContext context) {
        log.info("Selecting carrier for order: {}", order.getOrderKey());

        List<Carrier> availableCarriers = getCarriersForFacility(context.getFacilityCode(), context);

        if (availableCarriers.isEmpty()) {
            throw new OutboundOperationException("No carriers available for facility: " + context.getFacilityCode());
        }

        // Execute carrier selection plugin
        Optional<CarrierSelectionPlugin> plugin = pluginRegistry.getPlugin(
                CarrierSelectionPlugin.class, context);

        if (plugin.isPresent()) {
            CarrierSelectionResult result = plugin.get().selectCarrier(order, availableCarriers, context);
            log.info("Plugin selected carrier: {} service: {} for order: {}",
                    result.getSelectedCarrier().getCarrierCode(),
                    result.getSelectedService().getServiceCode(),
                    order.getOrderKey());
            return result;
        }

        // Default selection - use order's carrier or first available
        Carrier selectedCarrier = availableCarriers.stream()
                .filter(c -> c.getCarrierCode().equals(order.getCarrierCode()))
                .findFirst()
                .orElse(availableCarriers.get(0));

        CarrierService selectedService = selectedCarrier.getServices().stream()
                .filter(CarrierService::isActive)
                .findFirst()
                .orElseThrow(() -> new OutboundOperationException(
                        "No active services for carrier: " + selectedCarrier.getCarrierCode()));

        return CarrierSelectionResult.builder()
                .selectedCarrier(selectedCarrier)
                .selectedService(selectedService)
                .selectionReason("Default carrier from order")
                .build();
    }

    /**
     * Get available services for a carrier.
     */
    public List<CarrierService> getCarrierServices(String carrierCode) {
        Carrier carrier = carrierRepository.findByCode(carrierCode)
                .orElseThrow(() -> new OutboundOperationException("Carrier not found: " + carrierCode));

        return carrier.getServices().stream()
                .filter(CarrierService::isActive)
                .collect(Collectors.toList());
    }

    /**
     * Validate carrier change for an order/shipment.
     *
     * Legacy Reference: isp_ValidateCarrierChange
     */
    public CarrierChangeValidation validateCarrierChange(
            String currentCarrierCode,
            String newCarrierCode,
            String orderKey,
            OutboundPluginContext context) {

        log.info("Validating carrier change from {} to {} for order: {}",
                currentCarrierCode, newCarrierCode, orderKey);

        Carrier newCarrier = carrierRepository.findByCode(newCarrierCode)
                .orElseThrow(() -> new OutboundOperationException("Carrier not found: " + newCarrierCode));

        if (!newCarrier.isActive()) {
            return CarrierChangeValidation.builder()
                    .valid(false)
                    .errorMessage("Carrier is not active: " + newCarrierCode)
                    .build();
        }

        // Execute plugin validation
        Optional<CarrierSelectionPlugin> plugin = pluginRegistry.getPlugin(
                CarrierSelectionPlugin.class, context);

        if (plugin.isPresent()) {
            return plugin.get().validateCarrierChange(currentCarrierCode, newCarrierCode, orderKey, context);
        }

        return CarrierChangeValidation.builder()
                .valid(true)
                .newCarrier(newCarrier)
                .build();
    }

    /**
     * Create or update carrier.
     */
    @Transactional
    public Carrier saveCarrier(Carrier carrier) {
        log.info("Saving carrier: {}", carrier.getCarrierCode());
        return carrierRepository.save(carrier);
    }

    /**
     * Select carrier for an MBOL.
     *
     * Legacy Reference: Carrier routing logic from MBOL creation
     */
    @Transactional(readOnly = true)
    public CarrierSelectionResult selectCarrierForMbol(String mbolKey, OutboundPluginContext context) {
        log.info("Selecting carrier for MBOL: {}", mbolKey);

        MasterBillOfLading mbol = mbolRepository.findByKey(mbolKey)
                .orElseThrow(() -> new OutboundOperationException("MBOL not found: " + mbolKey));

        List<Carrier> availableCarriers = getCarriersForFacility(context.getFacilityCode(), context);

        if (availableCarriers.isEmpty()) {
            return CarrierSelectionResult.builder()
                    .selected(false)
                    .reason("No carriers available for facility")
                    .build();
        }

        // Try plugin selection first
        Optional<CarrierSelectionPlugin> plugin = pluginRegistry.getPlugin(
                CarrierSelectionPlugin.class, context);

        if (plugin.isPresent()) {
            CarrierSelectionResult result = plugin.get().selectCarrierForMbol(mbol, availableCarriers, context);
            if (result.isSelected()) {
                return result;
            }
        }

        // Default selection - use MBOL's existing carrier or first available
        Carrier selectedCarrier = availableCarriers.stream()
                .filter(c -> c.getCarrierCode().equals(mbol.getCarrierCode()))
                .findFirst()
                .orElse(availableCarriers.get(0));

        CarrierService selectedService = selectedCarrier.getServices().stream()
                .filter(CarrierService::isActive)
                .filter(s -> s.getServiceCode().equals(mbol.getCarrierServiceCode()))
                .findFirst()
                .orElse(selectedCarrier.getServices().stream()
                        .filter(CarrierService::isActive)
                        .findFirst()
                        .orElse(null));

        if (selectedService == null) {
            return CarrierSelectionResult.builder()
                    .selected(false)
                    .reason("No active services for carrier: " + selectedCarrier.getCarrierCode())
                    .build();
        }

        return CarrierSelectionResult.builder()
                .selected(true)
                .carrierCode(selectedCarrier.getCarrierCode())
                .carrierName(selectedCarrier.getName())
                .serviceCode(selectedService.getServiceCode())
                .serviceName(selectedService.getServiceName())
                .selectedCarrier(selectedCarrier)
                .selectedService(selectedService)
                .reason("Default carrier selection")
                .build();
    }

    /**
     * Change carrier for an MBOL.
     *
     * Legacy Reference: isp_UpdateCarrierService
     */
    @Transactional
    public MasterBillOfLading changeCarrier(String mbolKey, String newCarrierCode, String newServiceCode,
                                             OutboundPluginContext context) {
        log.info("Changing carrier for MBOL: {} to carrier: {} service: {}",
                mbolKey, newCarrierCode, newServiceCode);

        MasterBillOfLading mbol = mbolRepository.findByKey(mbolKey)
                .orElseThrow(() -> new OutboundOperationException("MBOL not found: " + mbolKey));

        // Validate the change
        CarrierChangeValidation validation = validateCarrierChange(
                mbol.getCarrierCode(), newCarrierCode, mbol.getMbolKey(), context);

        if (!validation.isValid()) {
            throw new OutboundOperationException("Carrier change not allowed: " + validation.getErrorMessage());
        }

        // Validate service exists
        Carrier newCarrier = carrierRepository.findByCode(newCarrierCode)
                .orElseThrow(() -> new OutboundOperationException("Carrier not found: " + newCarrierCode));

        CarrierService newService = newCarrier.getServices().stream()
                .filter(s -> s.getServiceCode().equals(newServiceCode))
                .findFirst()
                .orElseThrow(() -> new OutboundOperationException(
                        "Service not found: " + newServiceCode + " for carrier: " + newCarrierCode));

        // Update MBOL
        mbol.setCarrierCode(newCarrierCode);
        mbol.setCarrierServiceCode(newServiceCode);

        return mbolRepository.save(mbol);
    }

    /**
     * Calculate freight rates for an MBOL.
     *
     * Legacy Reference: Freight calculation from carrier integration
     */
    @Transactional(readOnly = true)
    public CarrierSelectionResult calculateFreightRates(String mbolKey, OutboundPluginContext context) {
        log.info("Calculating freight rates for MBOL: {}", mbolKey);

        MasterBillOfLading mbol = mbolRepository.findByKey(mbolKey)
                .orElseThrow(() -> new OutboundOperationException("MBOL not found: " + mbolKey));

        Carrier carrier = carrierRepository.findByCode(mbol.getCarrierCode())
                .orElseThrow(() -> new OutboundOperationException("Carrier not found: " + mbol.getCarrierCode()));

        // Try plugin for carrier-specific rate calculation
        Optional<CarrierSelectionPlugin> plugin = pluginRegistry.getPlugin(
                CarrierSelectionPlugin.class, context);

        if (plugin.isPresent()) {
            return plugin.get().calculateRates(mbol, carrier, context);
        }

        // Default calculation (placeholder - real implementation would call carrier API)
        BigDecimal estimatedRate = calculateDefaultRate(mbol);

        return CarrierSelectionResult.builder()
                .selected(true)
                .carrierCode(carrier.getCarrierCode())
                .carrierName(carrier.getName())
                .serviceCode(mbol.getCarrierServiceCode())
                .estimatedRate(estimatedRate)
                .currency("USD")
                .reason("Default rate calculation")
                .build();
    }

    private BigDecimal calculateDefaultRate(MasterBillOfLading mbol) {
        // Simple weight-based rate calculation
        BigDecimal weight = mbol.getTotalWeight() != null ? mbol.getTotalWeight() : BigDecimal.ZERO;
        BigDecimal ratePerPound = new BigDecimal("0.50");
        BigDecimal minimumCharge = new BigDecimal("10.00");

        BigDecimal calculated = weight.multiply(ratePerPound);
        return calculated.compareTo(minimumCharge) > 0 ? calculated : minimumCharge;
    }
}
