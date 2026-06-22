package com.maersk.wms.outbound.activity.impl;

import com.maersk.wms.outbound.activity.ShippingActivities;
import com.maersk.wms.outbound.domain.shipping.*;
import com.maersk.wms.outbound.plugin.OutboundPluginContext;
import com.maersk.wms.outbound.service.shipping.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ShippingActivities for Temporal workflow execution.
 * Delegates to shipping services with proper context.
 *
 * Legacy SP References:
 * - WM.lsp_WaveGenMBOL
 * - nsp_BackEndShipped
 * - isp_PrintCarrierLabel
 * - nsp_ShippingManifestDetails
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ShippingActivitiesImpl implements ShippingActivities {

    private final MbolService mbolService;
    private final LabelService labelService;
    private final ManifestService manifestService;
    private final CarrierManagementService carrierService;

    // ========== MBOL Operations ==========

    @Override
    public MbolResult generateMbolFromWave(String waveKey, String clientCode, String facilityCode) {
        log.info("Activity: generateMbolFromWave - wave: {} client: {} facility: {}",
                waveKey, clientCode, facilityCode);

        try {
            OutboundPluginContext context = buildContext(clientCode, facilityCode);
            MasterBillOfLading mbol = mbolService.generateFromWave(waveKey, context);

            return MbolResult.builder()
                    .success(true)
                    .mbolKey(mbol.getMbolKey())
                    .waveKey(mbol.getWaveKey())
                    .carrierCode(mbol.getCarrierCode())
                    .status(mbol.getStatus().name())
                    .totalOrders(mbol.getTotalOrders())
                    .totalCartons(mbol.getTotalCartons())
                    .totalWeight(mbol.getTotalWeight())
                    .build();
        } catch (Exception e) {
            log.error("Failed to generate MBOL from wave: {}", waveKey, e);
            return MbolResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public MbolResult populateMbolFromLoadPlan(String loadKey, String clientCode, String facilityCode) {
        log.info("Activity: populateMbolFromLoadPlan - load: {} client: {} facility: {}",
                loadKey, clientCode, facilityCode);

        try {
            OutboundPluginContext context = buildContext(clientCode, facilityCode);
            MasterBillOfLading mbol = mbolService.populateFromLoadPlan(loadKey, context);

            return MbolResult.builder()
                    .success(true)
                    .mbolKey(mbol.getMbolKey())
                    .loadKey(mbol.getLoadKey())
                    .status(mbol.getStatus().name())
                    .build();
        } catch (Exception e) {
            log.error("Failed to populate MBOL from load plan: {}", loadKey, e);
            return MbolResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public MbolValidation validateMbol(String mbolKey, String clientCode, String facilityCode) {
        log.info("Activity: validateMbol - mbol: {} client: {} facility: {}",
                mbolKey, clientCode, facilityCode);

        try {
            OutboundPluginContext context = buildContext(clientCode, facilityCode);
            MbolValidationResult result = mbolService.validateMbol(mbolKey, context);

            return MbolValidation.builder()
                    .valid(result.isValid())
                    .mbolKey(mbolKey)
                    .errors(result.getErrors())
                    .warnings(result.getWarnings())
                    .build();
        } catch (Exception e) {
            log.error("Failed to validate MBOL: {}", mbolKey, e);
            return MbolValidation.builder()
                    .valid(false)
                    .mbolKey(mbolKey)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public MbolResult shipMbol(String mbolKey, ShipConfirmInput input, String clientCode, String facilityCode) {
        log.info("Activity: shipMbol - mbol: {} client: {} facility: {}",
                mbolKey, clientCode, facilityCode);

        try {
            OutboundPluginContext context = buildContext(clientCode, facilityCode);

            ShipConfirmRequest request = ShipConfirmRequest.builder()
                    .trackingNumber(input.getTrackingNumber())
                    .proNumber(input.getProNumber())
                    .trailerNumber(input.getTrailerNumber())
                    .sealNumber(input.getSealNumber())
                    .build();

            MasterBillOfLading mbol = mbolService.shipMbol(mbolKey, request, context);

            return MbolResult.builder()
                    .success(true)
                    .mbolKey(mbol.getMbolKey())
                    .trackingNumber(mbol.getTrackingNumber())
                    .status(mbol.getStatus().name())
                    .build();
        } catch (Exception e) {
            log.error("Failed to ship MBOL: {}", mbolKey, e);
            return MbolResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public MbolResult moveOrderToNewMbol(String orderKey, String sourceMbolKey, String clientCode, String facilityCode) {
        log.info("Activity: moveOrderToNewMbol - order: {} from mbol: {} client: {} facility: {}",
                orderKey, sourceMbolKey, clientCode, facilityCode);

        try {
            OutboundPluginContext context = buildContext(clientCode, facilityCode);
            MasterBillOfLading newMbol = mbolService.moveOrderToNewMbol(orderKey, sourceMbolKey, context);

            return MbolResult.builder()
                    .success(true)
                    .mbolKey(newMbol.getMbolKey())
                    .status(newMbol.getStatus().name())
                    .build();
        } catch (Exception e) {
            log.error("Failed to move order to new MBOL: {}", orderKey, e);
            return MbolResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    // ========== Label Operations ==========

    @Override
    public LabelResult generateLabel(LabelInput labelInput, String clientCode, String facilityCode) {
        log.info("Activity: generateLabel - cbol: {} carrier: {} client: {} facility: {}",
                labelInput.getCbolKey(), labelInput.getCarrierCode(), clientCode, facilityCode);

        try {
            OutboundPluginContext context = buildContext(clientCode, facilityCode);

            LabelGenerationRequest request = LabelGenerationRequest.builder()
                    .cbolKey(labelInput.getCbolKey())
                    .mbolKey(labelInput.getMbolKey())
                    .carrierCode(labelInput.getCarrierCode())
                    .serviceCode(labelInput.getServiceCode())
                    .format(LabelFormat.valueOf(labelInput.getFormat()))
                    .shipToName(labelInput.getShipToName())
                    .shipToAddress1(labelInput.getShipToAddress1())
                    .shipToAddress2(labelInput.getShipToAddress2())
                    .shipToCity(labelInput.getShipToCity())
                    .shipToState(labelInput.getShipToState())
                    .shipToZip(labelInput.getShipToZip())
                    .shipToCountry(labelInput.getShipToCountry())
                    .weight(labelInput.getWeight())
                    .length(labelInput.getLength())
                    .width(labelInput.getWidth())
                    .height(labelInput.getHeight())
                    .build();

            ShippingLabel label = labelService.generateLabel(request, context);

            return LabelResult.builder()
                    .success(true)
                    .labelKey(label.getLabelKey())
                    .trackingNumber(label.getTrackingNumber())
                    .format(label.getFormat().name())
                    .labelContent(label.getLabelContent())
                    .build();
        } catch (Exception e) {
            log.error("Failed to generate label", e);
            return LabelResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public LabelResult generateLabelsForMbol(String mbolKey, String format, String clientCode, String facilityCode) {
        log.info("Activity: generateLabelsForMbol - mbol: {} format: {} client: {} facility: {}",
                mbolKey, format, clientCode, facilityCode);

        try {
            OutboundPluginContext context = buildContext(clientCode, facilityCode);
            List<ShippingLabel> labels = labelService.generateLabelsForMbol(
                    mbolKey, LabelFormat.valueOf(format), context);

            return LabelResult.builder()
                    .success(true)
                    .labelUrls(labels.stream()
                            .map(ShippingLabel::getTrackingNumber)
                            .collect(Collectors.toList()))
                    .build();
        } catch (Exception e) {
            log.error("Failed to generate labels for MBOL: {}", mbolKey, e);
            return LabelResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public LabelResult reprintLabel(String labelKey, String clientCode, String facilityCode) {
        log.info("Activity: reprintLabel - label: {} client: {} facility: {}",
                labelKey, clientCode, facilityCode);

        try {
            OutboundPluginContext context = buildContext(clientCode, facilityCode);
            ShippingLabel label = labelService.reprintLabel(labelKey, context);

            return LabelResult.builder()
                    .success(true)
                    .labelKey(label.getLabelKey())
                    .trackingNumber(label.getTrackingNumber())
                    .labelContent(label.getLabelContent())
                    .build();
        } catch (Exception e) {
            log.error("Failed to reprint label: {}", labelKey, e);
            return LabelResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public void voidLabel(String labelKey, String reason, String clientCode, String facilityCode) {
        log.info("Activity: voidLabel - label: {} reason: {} client: {} facility: {}",
                labelKey, reason, clientCode, facilityCode);

        OutboundPluginContext context = buildContext(clientCode, facilityCode);
        labelService.voidLabel(labelKey, reason, context);
    }

    // ========== Manifest Operations ==========

    @Override
    public ManifestResult createManifest(String carrierCode, String manifestType, String clientCode, String facilityCode) {
        log.info("Activity: createManifest - carrier: {} type: {} client: {} facility: {}",
                carrierCode, manifestType, clientCode, facilityCode);

        try {
            OutboundPluginContext context = buildContext(clientCode, facilityCode);
            ShippingManifest manifest = manifestService.createManifest(carrierCode, context);

            return ManifestResult.builder()
                    .success(true)
                    .manifestKey(manifest.getManifestKey())
                    .carrierCode(manifest.getCarrierCode())
                    .manifestType(manifest.getManifestType().name())
                    .status(manifest.getStatus().name())
                    .build();
        } catch (Exception e) {
            log.error("Failed to create manifest", e);
            return ManifestResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public ManifestResult addMbolToManifest(String manifestKey, String mbolKey, String clientCode, String facilityCode) {
        log.info("Activity: addMbolToManifest - manifest: {} mbol: {} client: {} facility: {}",
                manifestKey, mbolKey, clientCode, facilityCode);

        try {
            OutboundPluginContext context = buildContext(clientCode, facilityCode);
            ShippingManifest manifest = manifestService.addMbolToManifest(manifestKey, mbolKey, context);

            return ManifestResult.builder()
                    .success(true)
                    .manifestKey(manifest.getManifestKey())
                    .totalMbols(manifest.getMbolKeys().size())
                    .totalPackages(manifest.getTotalPackages())
                    .build();
        } catch (Exception e) {
            log.error("Failed to add MBOL to manifest: {}", mbolKey, e);
            return ManifestResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public ManifestResult closeManifest(String manifestKey, String clientCode, String facilityCode) {
        log.info("Activity: closeManifest - manifest: {} client: {} facility: {}",
                manifestKey, clientCode, facilityCode);

        try {
            OutboundPluginContext context = buildContext(clientCode, facilityCode);
            ShippingManifest manifest = manifestService.closeManifest(manifestKey, context);

            return ManifestResult.builder()
                    .success(true)
                    .manifestKey(manifest.getManifestKey())
                    .status(manifest.getStatus().name())
                    .build();
        } catch (Exception e) {
            log.error("Failed to close manifest: {}", manifestKey, e);
            return ManifestResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public ManifestResult transmitManifest(String manifestKey, String clientCode, String facilityCode) {
        log.info("Activity: transmitManifest - manifest: {} client: {} facility: {}",
                manifestKey, clientCode, facilityCode);

        try {
            OutboundPluginContext context = buildContext(clientCode, facilityCode);
            ManifestTransmissionResult result = manifestService.transmitManifest(manifestKey, context);

            return ManifestResult.builder()
                    .success(result.isSuccess())
                    .manifestKey(result.getManifestKey())
                    .transmissionId(result.getTransmissionId())
                    .transmittedAt(result.getTransmittedAt())
                    .errors(result.isSuccess() ? null : List.of(result.getErrorMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Failed to transmit manifest: {}", manifestKey, e);
            return ManifestResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public PickupResult schedulePickup(String manifestKey, PickupInput input, String clientCode, String facilityCode) {
        log.info("Activity: schedulePickup - manifest: {} client: {} facility: {}",
                manifestKey, clientCode, facilityCode);

        try {
            OutboundPluginContext context = buildContext(clientCode, facilityCode);

            com.maersk.wms.outbound.plugin.shipping.PickupRequest pickupRequest =
                    com.maersk.wms.outbound.plugin.shipping.PickupRequest.builder()
                            .pickupDate(LocalDate.parse(input.getPickupDate()))
                            .readyTime(LocalTime.parse(input.getReadyTime()))
                            .closeTime(LocalTime.parse(input.getCloseTime()))
                            .pickupLocation(input.getPickupLocation())
                            .contactName(input.getContactName())
                            .contactPhone(input.getContactPhone())
                            .totalPackages(input.getTotalPackages())
                            .totalWeight(input.getTotalWeight())
                            .weightUom(input.getWeightUom())
                            .residentialPickup(input.isResidentialPickup())
                            .specialInstructions(input.getSpecialInstructions())
                            .build();

            com.maersk.wms.outbound.plugin.shipping.PickupScheduleResult result =
                    manifestService.schedulePickup(manifestKey, pickupRequest, context);

            return PickupResult.builder()
                    .success(result.isSuccess())
                    .confirmationNumber(result.getConfirmationNumber())
                    .scheduledPickupTime(result.getScheduledPickupTime())
                    .driverName(result.getDriverName())
                    .driverPhone(result.getDriverPhone())
                    .errors(result.isSuccess() ? null : List.of(result.getErrorMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Failed to schedule pickup: {}", manifestKey, e);
            return PickupResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public void cancelPickup(String confirmationNumber, String clientCode, String facilityCode) {
        log.info("Activity: cancelPickup - confirmation: {} client: {} facility: {}",
                confirmationNumber, clientCode, facilityCode);

        OutboundPluginContext context = buildContext(clientCode, facilityCode);
        manifestService.cancelPickup(confirmationNumber, context);
    }

    // ========== Carrier Operations ==========

    @Override
    public CarrierResult selectCarrier(String mbolKey, String clientCode, String facilityCode) {
        log.info("Activity: selectCarrier - mbol: {} client: {} facility: {}",
                mbolKey, clientCode, facilityCode);

        try {
            OutboundPluginContext context = buildContext(clientCode, facilityCode);
            CarrierSelectionResult result = carrierService.selectCarrierForMbol(mbolKey, context);

            return CarrierResult.builder()
                    .success(result.isSelected())
                    .carrierCode(result.getCarrierCode())
                    .carrierName(result.getCarrierName())
                    .serviceCode(result.getServiceCode())
                    .serviceName(result.getServiceName())
                    .estimatedRate(result.getEstimatedRate())
                    .estimatedDeliveryDays(result.getEstimatedDeliveryDays())
                    .build();
        } catch (Exception e) {
            log.error("Failed to select carrier for MBOL: {}", mbolKey, e);
            return CarrierResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public CarrierResult changeCarrier(String mbolKey, String newCarrierCode, String newServiceCode,
                                        String clientCode, String facilityCode) {
        log.info("Activity: changeCarrier - mbol: {} newCarrier: {} client: {} facility: {}",
                mbolKey, newCarrierCode, clientCode, facilityCode);

        try {
            OutboundPluginContext context = buildContext(clientCode, facilityCode);
            carrierService.changeCarrier(mbolKey, newCarrierCode, newServiceCode, context);

            return CarrierResult.builder()
                    .success(true)
                    .carrierCode(newCarrierCode)
                    .serviceCode(newServiceCode)
                    .build();
        } catch (Exception e) {
            log.error("Failed to change carrier for MBOL: {}", mbolKey, e);
            return CarrierResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public FreightResult calculateFreight(String mbolKey, String clientCode, String facilityCode) {
        log.info("Activity: calculateFreight - mbol: {} client: {} facility: {}",
                mbolKey, clientCode, facilityCode);

        try {
            OutboundPluginContext context = buildContext(clientCode, facilityCode);
            CarrierSelectionResult result = carrierService.calculateFreightRates(mbolKey, context);

            return FreightResult.builder()
                    .success(result.isSelected())
                    .freightCharge(result.getEstimatedRate())
                    .currency(result.getCurrency())
                    .build();
        } catch (Exception e) {
            log.error("Failed to calculate freight for MBOL: {}", mbolKey, e);
            return FreightResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    // ========== Legacy Shipment Operations ==========

    @Override
    public ShipResult shipOrder(String orderNumber, List<String> cartonIds,
                                 String clientCode, String facilityCode) {
        log.info("Activity: shipOrder (legacy) - order: {} client: {} facility: {}",
                orderNumber, clientCode, facilityCode);

        // Legacy operation - delegate to modern MBOL flow
        // Would need to find/create MBOL for order first
        return ShipResult.builder()
                .success(false)
                .errors(List.of("Use shipMbol for modern shipping flow"))
                .build();
    }

    @Override
    public ShipResult createShipment(String orderNumber, String carrier, String shipMethod,
                                      String clientCode, String facilityCode) {
        log.info("Activity: createShipment (legacy) - order: {} carrier: {} client: {} facility: {}",
                orderNumber, carrier, clientCode, facilityCode);

        return ShipResult.builder()
                .success(false)
                .errors(List.of("Use generateMbolFromWave for modern shipping flow"))
                .build();
    }

    @Override
    public ShipResult addCartonToShipment(String shipmentId, String cartonId,
                                           String clientCode, String facilityCode) {
        log.info("Activity: addCartonToShipment (legacy) - shipment: {} carton: {} client: {} facility: {}",
                shipmentId, cartonId, clientCode, facilityCode);

        return ShipResult.builder()
                .success(false)
                .errors(List.of("Use addMbolToManifest for modern shipping flow"))
                .build();
    }

    @Override
    public ShipResult confirmShipment(String shipmentId, String clientCode, String facilityCode) {
        log.info("Activity: confirmShipment (legacy) - shipment: {} client: {} facility: {}",
                shipmentId, clientCode, facilityCode);

        return ShipResult.builder()
                .success(false)
                .errors(List.of("Use shipMbol for modern shipping flow"))
                .build();
    }

    @Override
    public void cancelShipment(String shipmentId, String clientCode, String facilityCode) {
        log.info("Activity: cancelShipment (legacy) - shipment: {} client: {} facility: {}",
                shipmentId, clientCode, facilityCode);

        // Legacy operation - would void labels and cancel MBOL
    }

    // ========== Helper Methods ==========

    private OutboundPluginContext buildContext(String clientCode, String facilityCode) {
        return OutboundPluginContext.builder()
                .clientCode(clientCode)
                .facilityCode(facilityCode)
                .userId("SYSTEM")
                .build();
    }
}
