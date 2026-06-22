package com.maersk.wms.picking.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ConfirmRequest {
    @NotBlank(message = "Task ID is required")
    private String taskId;

    @NotNull(message = "Picked quantity is required")
    @PositiveOrZero(message = "Picked quantity cannot be negative")
    private BigDecimal pickedQty;

    private BigDecimal requestedQty;
    private String fromLpn;
    private String toLpn;
    private String fromLocation;
    private String lot;
    private String shortReason;
}
