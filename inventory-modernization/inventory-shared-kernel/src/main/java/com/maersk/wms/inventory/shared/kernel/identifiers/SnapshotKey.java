package com.maersk.wms.inventory.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Inventory Snapshots.
 * Represents a point-in-time capture of inventory state.
 */
public record SnapshotKey(String value) {
    public SnapshotKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("SnapshotKey cannot be null or blank");
        }
    }
}
