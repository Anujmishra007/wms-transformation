package com.maersk.wms.outbound.workflow.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Label information for query response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabelInfo {

    private String labelKey;
    private String cbolKey;
    private String trackingNumber;
    private String carrierCode;
    private String serviceCode;

    private String format;
    private String status;
    private String labelUrl;

    private boolean printed;
    private int printAttempts;
    private LocalDateTime generatedAt;
    private LocalDateTime printedAt;
    private String printedBy;

    private boolean voided;
    private LocalDateTime voidedAt;
    private String voidedBy;
    private String voidReason;
}
