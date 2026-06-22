package com.maersk.wms.outbound.service.shipping;

import com.maersk.wms.outbound.domain.shipping.*;
import com.maersk.wms.outbound.domain.shipping.dto.LabelGenerationRequest;
import com.maersk.wms.outbound.domain.shipping.dto.LabelValidationResult;
import com.maersk.wms.outbound.domain.repository.MbolRepository;
import com.maersk.wms.outbound.plugin.OutboundPluginContext;
import com.maersk.wms.outbound.plugin.OutboundPluginRegistry;
import com.maersk.wms.outbound.plugin.PluginResult;
import com.maersk.wms.outbound.plugin.shipping.LabelGenerationPlugin;
import com.maersk.wms.outbound.service.OutboundOperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for shipping label operations.
 *
 * Legacy SP References:
 * - isp_PrintCarrierLabel - Print carrier label
 * - rdtfnc_ReprintCarrierLabel - Reprint carrier label
 * - isp_BT_Bartender_Shipper_Label_* - Client-specific label generation (100+ variants)
 * - nsp_GenLabelNo - Generate label number
 * - nsp_CartonManifestLabel - Carton manifest label
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LabelService {

    private final MbolRepository mbolRepository;
    private final OutboundPluginRegistry pluginRegistry;

    /**
     * Generate shipping label for a CBOL (package).
     *
     * Legacy Reference: isp_PrintCarrierLabel, isp_BT_Bartender_Shipper_Label_*
     */
    @Transactional
    public ShippingLabel generateLabel(LabelGenerationRequest request, OutboundPluginContext context) {
        log.info("Generating label for CBOL: {} carrier: {}", request.getCbolKey(), request.getCarrierCode());

        // Execute before label generation plugins
        PluginResult beforeResult = pluginRegistry.executeAll(
                LabelGenerationPlugin.class,
                context,
                plugin -> plugin.beforeLabelGeneration(request, context)
        );

        if (!beforeResult.isSuccess()) {
            throw new OutboundOperationException("Label generation blocked: " + beforeResult.getErrorMessage());
        }

        // Get label generation plugin for carrier
        Optional<LabelGenerationPlugin> labelPlugin = pluginRegistry.getPlugin(
                LabelGenerationPlugin.class, context);

        ShippingLabel label;
        if (labelPlugin.isPresent()) {
            // Plugin handles carrier API integration
            label = labelPlugin.get().generateLabel(request, context);
        } else {
            // Default label generation (internal tracking number)
            label = generateDefaultLabel(request, context);
        }

        // Execute after label generation plugins
        pluginRegistry.executeAll(
                LabelGenerationPlugin.class,
                context,
                plugin -> plugin.afterLabelGeneration(label, context)
        );

        log.info("Label generated: {} tracking: {}", label.getLabelKey(), label.getTrackingNumber());

        return label;
    }

    /**
     * Generate labels for all packages in an MBOL.
     */
    @Transactional
    public List<ShippingLabel> generateLabelsForMbol(String mbolKey, LabelFormat format, OutboundPluginContext context) {
        log.info("Generating labels for MBOL: {}", mbolKey);

        MasterBillOfLading mbol = mbolRepository.findByKey(mbolKey)
                .orElseThrow(() -> new OutboundOperationException("MBOL not found: " + mbolKey));

        List<ShippingLabel> labels = new ArrayList<>();

        for (CommercialBillOfLading cbol : mbol.getCbols()) {
            LabelGenerationRequest request = LabelGenerationRequest.builder()
                    .cbolKey(cbol.getCbolKey())
                    .mbolKey(mbolKey)
                    .carrierCode(mbol.getCarrierCode())
                    .serviceCode(mbol.getCarrierServiceCode())
                    .format(format)
                    .shipToName(cbol.getShipToName())
                    .shipToAddress1(cbol.getShipToAddress1())
                    .shipToAddress2(cbol.getShipToAddress2())
                    .shipToCity(cbol.getShipToCity())
                    .shipToState(cbol.getShipToState())
                    .shipToZip(cbol.getShipToZip())
                    .shipToCountry(cbol.getShipToCountry())
                    .weight(cbol.getWeight())
                    .length(cbol.getLength())
                    .width(cbol.getWidth())
                    .height(cbol.getHeight())
                    .build();

            labels.add(generateLabel(request, context));
        }

        return labels;
    }

    /**
     * Reprint an existing label.
     *
     * Legacy Reference: rdtfnc_ReprintCarrierLabel
     */
    @Transactional
    public ShippingLabel reprintLabel(String labelKey, OutboundPluginContext context) {
        log.info("Reprinting label: {}", labelKey);

        // Get existing label and update print count
        // In real implementation, would fetch from label repository
        ShippingLabel label = ShippingLabel.builder()
                .labelKey(labelKey)
                .printAttempts(1)
                .printedAt(LocalDateTime.now())
                .printedBy(context.getUserId())
                .status(LabelStatus.PRINTED)
                .build();

        // Plugin hook for reprint
        Optional<LabelGenerationPlugin> plugin = pluginRegistry.getPlugin(
                LabelGenerationPlugin.class, context);

        if (plugin.isPresent()) {
            label = plugin.get().reprintLabel(labelKey, context);
        }

        return label;
    }

    /**
     * Void a shipping label.
     */
    @Transactional
    public void voidLabel(String labelKey, String reason, OutboundPluginContext context) {
        log.info("Voiding label: {} reason: {}", labelKey, reason);

        // Plugin hook for void (carrier API call to void tracking number)
        Optional<LabelGenerationPlugin> plugin = pluginRegistry.getPlugin(
                LabelGenerationPlugin.class, context);

        if (plugin.isPresent()) {
            plugin.get().voidLabel(labelKey, reason, context);
        }

        log.info("Label voided: {}", labelKey);
    }

    /**
     * Get label by tracking number.
     */
    public Optional<ShippingLabel> getLabelByTrackingNumber(String trackingNumber) {
        // Would query label repository
        return Optional.empty();
    }

    /**
     * Validate label data before generation.
     */
    public LabelValidationResult validateLabelRequest(LabelGenerationRequest request, OutboundPluginContext context) {
        LabelValidationResult.LabelValidationResultBuilder resultBuilder = LabelValidationResult.builder()
                .valid(true);

        // Address validation
        if (request.getShipToAddress1() == null || request.getShipToAddress1().isEmpty()) {
            resultBuilder.valid(false);
            resultBuilder.error("Ship-to address is required");
        }

        if (request.getShipToCity() == null || request.getShipToCity().isEmpty()) {
            resultBuilder.valid(false);
            resultBuilder.error("Ship-to city is required");
        }

        if (request.getShipToZip() == null || request.getShipToZip().isEmpty()) {
            resultBuilder.valid(false);
            resultBuilder.error("Ship-to postal code is required");
        }

        // Weight validation
        if (request.getWeight() == null || request.getWeight().signum() <= 0) {
            resultBuilder.valid(false);
            resultBuilder.error("Package weight is required");
        }

        // Plugin validation
        Optional<LabelGenerationPlugin> plugin = pluginRegistry.getPlugin(
                LabelGenerationPlugin.class, context);

        if (plugin.isPresent()) {
            LabelValidationResult pluginResult = plugin.get().validateLabelRequest(request, context);
            if (!pluginResult.isValid()) {
                return pluginResult;
            }
        }

        return resultBuilder.build();
    }

    private ShippingLabel generateDefaultLabel(LabelGenerationRequest request, OutboundPluginContext context) {
        // Generate internal tracking number
        String trackingNumber = generateTrackingNumber(request.getCarrierCode());

        return ShippingLabel.builder()
                .cbolKey(request.getCbolKey())
                .mbolKey(request.getMbolKey())
                .trackingNumber(trackingNumber)
                .carrierCode(request.getCarrierCode())
                .serviceCode(request.getServiceCode())
                .format(request.getFormat())
                .status(LabelStatus.GENERATED)
                .generatedAt(LocalDateTime.now())
                .addWho(context.getUserId())
                .addDate(LocalDateTime.now())
                .build();
    }

    private String generateTrackingNumber(String carrierCode) {
        // Simple tracking number generation - in real implementation would use NCOUNTER
        return carrierCode + "-" + System.currentTimeMillis();
    }
}
