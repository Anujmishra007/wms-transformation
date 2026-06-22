package com.maersk.wms.inventory.shared.kernel.valueobjects;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;

import java.util.List;

/**
 * Value object representing the result of an inventory allocation operation.
 */
public record AllocationResult(
        AllocationKey allocationKey,
        SkuKey skuKey,
        OrderKey orderKey,
        boolean fullyAllocated,
        Quantity requestedQuantity,
        Quantity allocatedQuantity,
        Quantity shortQuantity,
        List<AllocationLine> allocationLines,
        String message
) {

    /**
     * Alias for shortQuantity for compatibility.
     */
    public Quantity shortageQuantity() {
        return shortQuantity;
    }

    /**
     * Represents a single allocation from one inventory record.
     */
    public record AllocationLine(
            InventoryKey inventoryKey,
            LotKey lotKey,
            LocationKey locationKey,
            LpnKey lpnKey,
            Quantity allocatedQuantity,
            LottableAttributes lottables
    ) {
        /**
         * Alias for allocatedQuantity for compatibility.
         */
        public Quantity quantity() {
            return allocatedQuantity;
        }

        /**
         * Alternative constructor without lottables.
         */
        public static AllocationLine of(InventoryKey inventoryKey, LocationKey locationKey,
                                         LotKey lotKey, LpnKey lpnKey, Quantity quantity) {
            return new AllocationLine(inventoryKey, lotKey, locationKey, lpnKey, quantity, null);
        }
    }

    public static AllocationResult success(AllocationKey key, SkuKey skuKey, OrderKey orderKey,
                                           Quantity requested, List<AllocationLine> lines) {
        Quantity allocated = lines.stream()
                .map(AllocationLine::allocatedQuantity)
                .reduce(Quantity.ZERO, Quantity::add);

        return new AllocationResult(
                key,
                skuKey,
                orderKey,
                true,
                requested,
                allocated,
                Quantity.ZERO,
                lines,
                "Allocation successful"
        );
    }

    public static AllocationResult success(AllocationKey key, Quantity requested, List<AllocationLine> lines) {
        return success(key, null, null, requested, lines);
    }

    public static AllocationResult partial(AllocationKey key, SkuKey skuKey, OrderKey orderKey,
                                            Quantity requested, Quantity allocated,
                                            List<AllocationLine> lines) {
        Quantity shortQty = requested.subtract(allocated);
        return new AllocationResult(
                key,
                skuKey,
                orderKey,
                false,
                requested,
                allocated,
                shortQty,
                lines,
                "Partial allocation - short " + shortQty.value() + " " + shortQty.uom()
        );
    }

    public static AllocationResult partial(AllocationKey key, Quantity requested, Quantity allocated,
                                            List<AllocationLine> lines) {
        return partial(key, null, null, requested, allocated, lines);
    }

    public static AllocationResult failed(Quantity requested, String reason) {
        return new AllocationResult(
                null,
                null,
                null,
                false,
                requested,
                Quantity.ZERO,
                requested,
                List.of(),
                reason
        );
    }

    public boolean isShort() {
        return !fullyAllocated && shortQuantity.isPositive();
    }

    public int getAllocationCount() {
        return allocationLines.size();
    }
}
