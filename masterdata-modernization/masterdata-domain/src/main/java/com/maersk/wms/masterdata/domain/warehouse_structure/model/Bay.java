package com.maersk.wms.masterdata.domain.warehouse_structure.model;

import com.maersk.wms.masterdata.shared.kernel.identifiers.*;

import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Bay entity representing a rack bay in an aisle.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bay {

    private BayKey bayKey;
    private AisleKey aisleKey;
    private WarehouseKey warehouseKey;

    private String bayName;
    private String bayCode;
    private int bayNumber;

    // Configuration
    private String bayType; // SELECTIVE, DRIVE_IN, PUSH_BACK, PALLET_FLOW
    private int bayDepth; // Number of pallets deep
    private String side; // LEFT, RIGHT, BOTH

    // Dimensions
    private int heightInches;
    private int widthInches;
    private int depthInches;

    // Child Entities
    @Builder.Default
    private List<Level> levels = new ArrayList<>();

    // Status
    private boolean active;

    // Audit
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;

    // Business Methods
    public void activate() {
        this.active = true;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }

    public void addLevel(Level level) {
        this.levels.add(level);
        this.updatedAt = Instant.now();
    }

    public int getLevelCount() {
        return levels.size();
    }

    public int getTotalPositions() {
        return levels.stream()
                .mapToInt(Level::getPositionCount)
                .sum();
    }
}
