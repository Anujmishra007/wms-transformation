package com.maersk.wms.inventory.shared.kernel.valueobjects;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;

import java.util.List;

/**
 * Value object representing the result of an inventory allocation operation.
 */
public record AllocationResult(
        AllocationKey allocationKey,
        boolean fullyAllocated,
        Quantity requestedQuantity,
        Quantity allocatedQuantity,
        Quantity shortQuantity,
        List<AllocationLine> allocationLines,
        String message
) {

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
    ) {}

    public static AllocationResult success(AllocationKey key, Quantity requested, List<AllocationLine> lines) {
        Quantity allocated = lines.stream()
                .map(AllocationLine::allocatedQuantity)
                .reduce(Quantity.ZERO, Quantity::add);

        return new AllocationResult(
                key,
                true,
                requested,
                allocated,
                Quantity.ZERO,
                lines,
                "Allocation successful"
        );
    }

    public static AllocationResult partial(AllocationKey key, Quantity requested, Quantity allocated,
                                            List<AllocationLine> lines) {
        Quantity shortQty = requested.subtract(allocated);
        return new AllocationResult(
                key,
                false,
                requested,
                allocated,
                shortQty,
                lines,
                "Partial allocation - short " + shortQty.value() + " " + shortQty.uom()
        );
    }

    public static AllocationResult failed(Quantity requested, String reason) {
        return new AllocationResult(
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
