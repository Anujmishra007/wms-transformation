package com.maersk.wms.inbound.domain.operations_service.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Result DTO for credit memo generation.
 */
@Data
@Builder
public class CreditMemoResult {
    private String creditMemoNumber;
    private String returnKey;
    private String orderKey;
    private BigDecimal creditAmount;
    private String currencyCode;
    private String status;
    private boolean success;
    private String errorMessage;
    private Instant createdAt;
}
