package com.maersk.wms.task.shared.kernel.valueobjects;

import com.maersk.wms.task.shared.kernel.identifiers.*;

/**
 * Value object containing business context for task execution.
 */
public record TaskContext(
        ContextType contextType,
        String sourceKey,
        String sourceType,
        OrderKey orderKey,
        WaveKey waveKey,
        LpnKey lpnKey,
        SkuKey skuKey,
        LocationKey fromLocation,
        LocationKey toLocation,
        ZoneKey zone,
        String customerId,
        String carrierId,
        String priority,
        String additionalData
) {

    public static TaskContext forOrder(OrderKey orderKey, WaveKey waveKey) {
        return new TaskContext(ContextType.ORDER, orderKey.value(), "ORDER",
                orderKey, waveKey, null, null, null, null, null, null, null, null, null);
    }

    public static TaskContext forInventory(LpnKey lpn, LocationKey from, LocationKey to) {
        return new TaskContext(ContextType.INVENTORY, lpn.value(), "LPN",
                null, null, lpn, null, from, to, null, null, null, null, null);
    }

    public static TaskContext forShipment(String shipmentKey, String carrierId) {
        return new TaskContext(ContextType.SHIPMENT, shipmentKey, "SHIPMENT",
                null, null, null, null, null, null, null, null, carrierId, null, null);
    }

    public static TaskContext forAsn(String asnKey, LpnKey lpn) {
        return new TaskContext(ContextType.ASN, asnKey, "ASN",
                null, null, lpn, null, null, null, null, null, null, null, null);
    }

    public enum ContextType {
        ORDER, INVENTORY, SHIPMENT, ASN, USER, CUSTOM
    }
}
