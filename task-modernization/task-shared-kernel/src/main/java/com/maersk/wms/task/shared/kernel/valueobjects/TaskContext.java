package com.maersk.wms.task.shared.kernel.valueobjects;

import com.maersk.wms.task.shared.kernel.identifiers.*;

import java.util.Map;

/**
 * Value object containing business context for task execution.
 * Provides a flexible structure to hold different context types (order, inventory, shipment, etc.)
 */
public record TaskContext(
        String sourceType,
        String sourceKey,
        String waveKey,
        String customerId,
        String priority,
        String lpnKey,
        String fromLocation,
        String toLocation,
        String skuKey,
        Map<String, Object> attributes
) {

    public static TaskContext forOrder(OrderKey orderKey, WaveKey waveKey) {
        return new TaskContext("ORDER", orderKey.value(),
                waveKey != null ? waveKey.value() : null,
                null, null, null, null, null, null, Map.of());
    }

    public static TaskContext forInventory(LpnKey lpn, LocationKey from, LocationKey to) {
        return new TaskContext("INVENTORY", lpn.value(), null, null, null,
                lpn.value(),
                from != null ? from.value() : null,
                to != null ? to.value() : null,
                null, Map.of());
    }

    public static TaskContext forShipment(String shipmentKey, String carrierId) {
        return new TaskContext("SHIPMENT", shipmentKey, null, null, null,
                null, null, null, null, Map.of("carrierId", carrierId));
    }

    public static TaskContext forAsn(String asnKey, LpnKey lpn) {
        return new TaskContext("ASN", asnKey, null, null, null,
                lpn != null ? lpn.value() : null,
                null, null, null, Map.of());
    }

    public enum ContextType {
        ORDER, INVENTORY, SHIPMENT, ASN, USER, CUSTOM
    }
}
