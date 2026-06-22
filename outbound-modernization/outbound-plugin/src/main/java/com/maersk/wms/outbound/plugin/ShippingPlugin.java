package com.maersk.wms.outbound.plugin;

import com.maersk.wms.outbound.domain.Order;
import com.maersk.wms.outbound.domain.Shipment;

/**
 * Plugin interface for shipping operations.
 * Allows client-specific customizations for shipment processing.
 */
public interface ShippingPlugin extends OutboundPlugin {

    /**
     * Called before shipment is created.
     */
    default PluginResult beforeShipmentCreate(Order order, OutboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after shipment is created.
     */
    default PluginResult afterShipmentCreate(Shipment shipment, OutboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called before manifest is generated.
     */
    default PluginResult beforeManifest(Shipment shipment, OutboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after manifest is generated.
     */
    default PluginResult afterManifest(Shipment shipment, OutboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called before shipment is confirmed.
     */
    default PluginResult beforeShipConfirm(Shipment shipment, OutboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after shipment is confirmed.
     */
    default PluginResult afterShipConfirm(Shipment shipment, OutboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Select carrier for shipment.
     */
    default String selectCarrier(Order order, OutboundPluginContext context) {
        return order.getCarrierCode();
    }

    /**
     * Calculate freight charges.
     */
    default java.math.BigDecimal calculateFreight(Shipment shipment, OutboundPluginContext context) {
        return java.math.BigDecimal.ZERO;
    }
}
