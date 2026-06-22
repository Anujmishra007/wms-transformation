package com.maersk.wms.outbound.plugin.shipping;

import com.maersk.wms.outbound.domain.shipping.ShippingLabel;
import com.maersk.wms.outbound.plugin.OutboundPlugin;
import com.maersk.wms.outbound.plugin.OutboundPluginContext;
import com.maersk.wms.outbound.plugin.PluginResult;
import com.maersk.wms.outbound.domain.shipping.dto.AddressValidationResult;
import com.maersk.wms.outbound.domain.shipping.dto.LabelGenerationRequest;
import com.maersk.wms.outbound.domain.shipping.dto.LabelValidationResult;
import com.maersk.wms.outbound.domain.shipping.dto.TrackingInfo;

/**
 * Plugin interface for shipping label generation.
 * Handles carrier API integration for label generation.
 *
 * Legacy Reference:
 * - isp_PrintCarrierLabel
 * - isp_BT_Bartender_Shipper_Label_* (100+ client-specific variants)
 * - rdtfnc_ReprintCarrierLabel
 */
public interface LabelGenerationPlugin extends OutboundPlugin {

    /**
     * Called before label generation.
     * Allows validation or modification of label request.
     */
    default PluginResult beforeLabelGeneration(LabelGenerationRequest request, OutboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Generate shipping label via carrier API.
     * This is the core method that calls the carrier's API.
     *
     * @param request Label generation request with all details
     * @param context Plugin context
     * @return Generated label with tracking number and label data
     */
    ShippingLabel generateLabel(LabelGenerationRequest request, OutboundPluginContext context);

    /**
     * Called after label generation.
     * Can be used for logging, notifications, etc.
     */
    default PluginResult afterLabelGeneration(ShippingLabel label, OutboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Validate label request before generation.
     * Checks address validity, carrier requirements, etc.
     */
    default LabelValidationResult validateLabelRequest(LabelGenerationRequest request, OutboundPluginContext context) {
        return LabelValidationResult.success();
    }

    /**
     * Reprint an existing label.
     * May need to call carrier API for fresh copy or use cached data.
     */
    default ShippingLabel reprintLabel(String labelKey, OutboundPluginContext context) {
        throw new UnsupportedOperationException("Reprint not implemented");
    }

    /**
     * Void a shipping label.
     * Calls carrier API to cancel the tracking number.
     */
    default void voidLabel(String labelKey, String reason, OutboundPluginContext context) {
        // Default: no-op
    }

    /**
     * Get tracking information for a label.
     */
    default TrackingInfo getTrackingInfo(String trackingNumber, OutboundPluginContext context) {
        return null;
    }

    /**
     * Check if carrier supports address validation.
     */
    default boolean supportsAddressValidation(String carrierCode) {
        return false;
    }

    /**
     * Validate address via carrier API.
     */
    default AddressValidationResult validateAddress(LabelGenerationRequest request, OutboundPluginContext context) {
        return AddressValidationResult.builder()
                .valid(true)
                .build();
    }

    /**
     * Get supported label formats for carrier.
     */
    default String[] getSupportedLabelFormats(String carrierCode) {
        return new String[]{"PDF", "ZPL"};
    }
}
