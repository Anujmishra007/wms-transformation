package com.maersk.wms.outbound.plugin;

import com.maersk.wms.outbound.domain.Allocation;
import com.maersk.wms.outbound.domain.Order;

import java.util.List;

/**
 * Plugin interface for packing operations.
 * Allows client-specific customizations for packing and cartonization.
 */
public interface PackingPlugin extends OutboundPlugin {

    /**
     * Called before packing starts.
     */
    default PluginResult beforePack(Order order, List<Allocation> allocations, OutboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after packing completes.
     */
    default PluginResult afterPack(Order order, List<String> cartonIds, OutboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Determine carton type for items.
     */
    default String determineCartonType(List<Allocation> allocations, OutboundPluginContext context) {
        return "STANDARD";
    }

    /**
     * Calculate cartonization - how items should be grouped into cartons.
     */
    default List<CartonPlan> planCartonization(Order order, List<Allocation> allocations, OutboundPluginContext context) {
        return List.of(CartonPlan.builder()
                .cartonType("STANDARD")
                .allocations(allocations)
                .build());
    }

    /**
     * Validate pack operation.
     */
    default PluginResult validatePack(String cartonId, List<Allocation> contents, OutboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Check if gift wrap is required.
     */
    default boolean requiresGiftWrap(Order order, OutboundPluginContext context) {
        return false;
    }

    /**
     * Carton plan for cartonization.
     */
    @lombok.Data
    @lombok.Builder
    class CartonPlan {
        private String cartonType;
        private List<Allocation> allocations;
        private boolean requiresSpecialHandling;
        private String packingInstructions;
    }
}
