package com.maersk.wms.inbound.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request DTO for receiving inventory.
 */
@Data
public class ReceiveInventoryRequest {

    @NotBlank(message = "SKU is required")
    private String sku;

    private String lot;
    private String lpn;

    @NotNull(message = "Received quantity is required")
    @Positive(message = "Received quantity must be positive")
    private BigDecimal receivedQty;

    private BigDecimal damagedQty;
    private String location;
    private String conditionCode;
    private LocalDateTime expirationDate;
    private LocalDateTime manufactureDate;
    private String countryOfOrigin;
    private String vendorLot;

    // Lottable attributes
    private String lottable01;
    private String lottable02;
    private String lottable03;
    private String lottable04;
    private String lottable05;
}
