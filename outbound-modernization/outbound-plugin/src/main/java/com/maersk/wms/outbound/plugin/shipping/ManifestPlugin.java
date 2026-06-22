package com.maersk.wms.outbound.plugin.shipping;

import com.maersk.wms.outbound.domain.shipping.ShippingManifest;
import com.maersk.wms.outbound.plugin.OutboundPlugin;
import com.maersk.wms.outbound.plugin.OutboundPluginContext;
import com.maersk.wms.outbound.plugin.PluginResult;
import com.maersk.wms.outbound.domain.shipping.dto.ManifestRequirements;
import com.maersk.wms.outbound.domain.shipping.dto.ManifestTransmissionResult;
import com.maersk.wms.outbound.domain.shipping.dto.PickupRequest;
import com.maersk.wms.outbound.domain.shipping.dto.PickupScheduleResult;

/**
 * Plugin interface for shipping manifest operations.
 * Handles carrier end-of-day manifest close and transmission.
 *
 * Legacy Reference:
 * - nsp_ShippingManifestDetails
 * - nsp_GetLoadManifest
 * - isp_shipping_manifest_by_load_*
 * - rdtfnc_PrtPltManifest
 */
public interface ManifestPlugin extends OutboundPlugin {

    /**
     * Called when a new manifest is created.
     */
    default PluginResult onManifestCreate(ShippingManifest manifest, OutboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called before manifest close.
     * Can validate manifest completeness.
     */
    default PluginResult beforeManifestClose(ShippingManifest manifest, OutboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after manifest close.
     * Typically used to transmit manifest to carrier.
     */
    default PluginResult afterManifestClose(ShippingManifest manifest, OutboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Transmit manifest to carrier API.
     * This is the core carrier integration point.
     */
    default ManifestTransmissionResult transmitManifest(ShippingManifest manifest, OutboundPluginContext context) {
        return ManifestTransmissionResult.builder()
                .success(true)
                .manifestKey(manifest.getManifestKey())
                .build();
    }

    /**
     * Generate manifest document (PDF, etc.).
     */
    default byte[] generateManifestDocument(ShippingManifest manifest, String format, OutboundPluginContext context) {
        return null;
    }

    /**
     * Schedule pickup with carrier.
     */
    default PickupScheduleResult schedulePickup(ShippingManifest manifest, PickupRequest request, OutboundPluginContext context) {
        return PickupScheduleResult.builder()
                .success(true)
                .build();
    }

    /**
     * Cancel scheduled pickup.
     */
    default void cancelPickup(String pickupConfirmationNumber, OutboundPluginContext context) {
        // Default: no-op
    }

    /**
     * Get carrier-specific manifest requirements.
     */
    default ManifestRequirements getManifestRequirements(String carrierCode, OutboundPluginContext context) {
        return ManifestRequirements.builder()
                .requiresTransmission(true)
                .build();
    }
}
