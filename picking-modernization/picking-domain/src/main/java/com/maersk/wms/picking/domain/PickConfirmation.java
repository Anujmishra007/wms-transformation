package com.maersk.wms.picking.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pick confirmation request - captures pick completion details.
 */
@Data
@Builder
public class PickConfirmation {

    @NotBlank(message = "Task ID is required")
    private String taskId;

    @NotNull(message = "Picked quantity is required")
    @PositiveOrZero(message = "Picked quantity cannot be negative")
    private BigDecimal pickedQty;

    /** Source location confirmed */
    private String fromLocation;

    /** Source LPN */
    private String fromLpn;

    /** Destination LPN (cart, tote, etc.) */
    private String toLpn;

    /** Destination location */
    private String toLocation;

    /** Lot number picked */
    private String lot;

    /** Serial numbers picked (comma-separated for multiple) */
    private String serialNumbers;

    /** User performing the pick */
    private String userId;

    /** Equipment used */
    private String equipmentId;

    /** Confirmation timestamp */
    private LocalDateTime confirmedAt;

    /** Short pick reason code */
    private String shortReason;

    /** Short pick quantity */
    private BigDecimal shortQty;

    /** Damage reason if damaged pick */
    private String damageReason;

    /** Override code for exceptions */
    private String overrideCode;

    /** Supervisor ID for override */
    private String supervisorId;

    /**
     * Check if this is a full pick.
     */
    public boolean isFullPick(BigDecimal requestedQty) {
        return pickedQty != null && requestedQty != null
               && pickedQty.compareTo(requestedQty) >= 0;
    }

    /**
     * Check if this is a short pick.
     */
    public boolean isShortPick(BigDecimal requestedQty) {
        return pickedQty != null && requestedQty != null
               && pickedQty.compareTo(requestedQty) < 0;
    }

    /**
     * Check if this is a zero pick.
     */
    public boolean isZeroPick() {
        return pickedQty == null || pickedQty.compareTo(BigDecimal.ZERO) == 0;
    }
}
