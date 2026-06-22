package com.maersk.wms.inbound.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Request DTO for creating a receipt.
 */
@Data
public class CreateReceiptRequest {

    private String externalReceiptKey;

    @NotBlank(message = "Storer key is required")
    private String storerKey;

    private String receiptType;
    private String poKey;
    private String asnKey;
    private String carrierKey;
    private String trailerNumber;
    private String sealNumber;
    private String door;
    private LocalDateTime expectedArrivalDate;
    private String notes;
}
