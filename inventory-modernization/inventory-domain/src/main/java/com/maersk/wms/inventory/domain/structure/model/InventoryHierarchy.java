package com.maersk.wms.inventory.domain.structure.model;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;

import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing inventory hierarchy/nesting.
 * Supports parent-child relationships: Pallet → Case → Inner Pack → Each.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryHierarchy {

    private NestingKey nestingKey;

    // Parent Container
    private LpnKey parentLpnKey;
    private ContainerType parentType;

    // Child Container
    private LpnKey childLpnKey;
    private ContainerType childType;

    // Relationship
    private int nestingLevel;           // 1 = direct child, 2 = grandchild, etc.
    private int sequenceNumber;         // Position within parent
    private int quantity;               // Number of child units

    // Location (denormalized for performance)
    private LocationKey locationKey;
    private WarehouseKey warehouseKey;

    // Status
    private NestingStatus status;

    // Audit
    private Instant nestedAt;
    private UserKey nestedBy;
    private Instant unnestedAt;
    private UserKey unnestedBy;

    public enum ContainerType {
        PALLET,         // Top-level container
        CASE,           // Case/carton
        INNER_PACK,     // Inner pack
        EACH,           // Individual item
        TOTE,           // Tote/bin
        MIXED           // Mixed container
    }

    public enum NestingStatus {
        ACTIVE,         // Currently nested
        UNNESTED,       // Has been unnested
        INVALID         // Invalid nesting
    }

    /**
     * Check if this is a valid nesting hierarchy.
     * Pallet > Case > Inner Pack > Each
     */
    public boolean isValidHierarchy() {
        if (parentType == null || childType == null) return false;

        return switch (parentType) {
            case PALLET -> childType == ContainerType.CASE
                    || childType == ContainerType.INNER_PACK
                    || childType == ContainerType.EACH
                    || childType == ContainerType.TOTE;
            case CASE -> childType == ContainerType.INNER_PACK
                    || childType == ContainerType.EACH;
            case INNER_PACK -> childType == ContainerType.EACH;
            case TOTE -> childType == ContainerType.CASE
                    || childType == ContainerType.INNER_PACK
                    || childType == ContainerType.EACH;
            case MIXED -> true; // Mixed containers can hold anything
            default -> false;
        };
    }

    /**
     * Unnest child from parent.
     */
    public void unnest(UserKey unnestedByUser) {
        this.status = NestingStatus.UNNESTED;
        this.unnestedAt = Instant.now();
        this.unnestedBy = unnestedByUser;
    }

    /**
     * Create nesting relationship.
     */
    public static InventoryHierarchy createNesting(
            LpnKey parentLpn, ContainerType parentType,
            LpnKey childLpn, ContainerType childType,
            LocationKey location, WarehouseKey warehouse,
            int sequence, int quantity, UserKey nestedBy) {

        InventoryHierarchy hierarchy = InventoryHierarchy.builder()
                .nestingKey(new NestingKey(java.util.UUID.randomUUID().toString()))
                .parentLpnKey(parentLpn)
                .parentType(parentType)
                .childLpnKey(childLpn)
                .childType(childType)
                .locationKey(location)
                .warehouseKey(warehouse)
                .nestingLevel(1)
                .sequenceNumber(sequence)
                .quantity(quantity)
                .status(NestingStatus.ACTIVE)
                .nestedAt(Instant.now())
                .nestedBy(nestedBy)
                .build();

        if (!hierarchy.isValidHierarchy()) {
            throw new IllegalArgumentException(
                    "Invalid nesting: " + parentType + " cannot contain " + childType);
        }

        return hierarchy;
    }
}
