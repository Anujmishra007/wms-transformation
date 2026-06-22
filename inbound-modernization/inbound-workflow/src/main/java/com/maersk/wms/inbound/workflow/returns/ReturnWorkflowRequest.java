package com.maersk.wms.inbound.workflow.returns;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Request object for return workflow.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnWorkflowRequest {

    // Context
    private String clientCode;
    private String countryCode;
    private String warehouseCode;
    private String userId;

    // Return type
    private String returnType;  // TRADE_RETURN, ECOM_RETURN, PIECE_RETURN

    // Original order reference
    private String originalOrderKey;
    private String originalOrderNumber;
    private String originalTrackingNumber;

    // RMA info
    private String rmaNumber;
    private LocalDateTime rmaDate;
    private LocalDateTime rmaExpiryDate;

    // Return reason
    private String returnReasonCode;
    private String returnReasonDescription;

    // Customer info
    private String storerKey;
    private String customerKey;
    private String customerName;
    private String customerPhone;
    private String customerEmail;

    // Ship from address
    private String shipFromName;
    private String shipFromAddress1;
    private String shipFromAddress2;
    private String shipFromCity;
    private String shipFromState;
    private String shipFromZip;
    private String shipFromCountry;

    // Carrier info
    private String carrierCode;
    private String trackingNumber;
    private LocalDateTime expectedArrivalDate;

    // Expected lines (if known)
    private List<ReturnLineRequest> expectedLines;

    // Workflow options
    private boolean autoStartReceiving;
    private boolean autoStartInspection;
    private boolean autoAssignDisposition;
    private boolean autoClose;
    private boolean requiresInspection;
    private boolean generateCreditMemo;

    // Notes
    private String notes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReturnLineRequest {
        private String sku;
        private String skuDescription;
        private BigDecimal expectedQty;
        private String returnReasonCode;
        private String originalOrderLineNumber;
        private BigDecimal unitPrice;
        private String lot;
        private String serialNumber;
    }
}
