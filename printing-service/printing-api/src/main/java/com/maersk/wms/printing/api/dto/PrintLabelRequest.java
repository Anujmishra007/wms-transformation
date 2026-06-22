package com.maersk.wms.printing.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Map;

/**
 * Request DTO for printing labels.
 * Supports LPN, SHIPPING, LOCATION, and ITEM label types.
 */
@Data
public class PrintLabelRequest {

    @NotBlank(message = "Label type is required")
    private String labelType;  // LPN, SHIPPING, LOCATION, ITEM

    @NotBlank(message = "Printer name is required")
    private String printerName;

    @Positive(message = "Copies must be positive")
    private int copies = 1;

    private Map<String, String> labelData;

    private String templateName;
}
