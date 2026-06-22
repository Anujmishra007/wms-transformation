package com.maersk.wms.inbound.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Temporal activity interface for return operations.
 *
 * Legacy SP References:
 * - rdtfnc_Return (5,547 lines) - Standard return
 * - rdtfnc_EcomReturn (6,099 lines) - E-commerce return
 * - rdtfnc_PieceReturn - Piece-level return
 * - rdtfnc_ReturnByTrackNo - Return by tracking number
 * - rdt_Return_V7_Inspect - Return inspection
 * - rdt_Return_V7_Close - Return closure
 */
@ActivityInterface
public interface ReturnActivities {

    // ========== Return Creation ==========

    @ActivityMethod
    ReturnResult createReturn(CreateReturnInput input, String clientCode, String warehouseCode);

    @ActivityMethod
    ReturnResult populateFromOrder(String orderKey, String storerKey, String returnType,
                                    String clientCode, String warehouseCode);

    @ActivityMethod
    ReturnResult findByTrackingNumber(String trackingNumber, String clientCode, String warehouseCode);

    @ActivityMethod
    ReturnResult findByRmaNumber(String rmaNumber, String clientCode, String warehouseCode);

    // ========== Return Receiving ==========

    @ActivityMethod
    ReturnResult startReceiving(String returnKey, String clientCode, String warehouseCode);

    @ActivityMethod
    ReturnLineResult receiveLineItem(ReceiveLineInput input, String clientCode, String warehouseCode);

    @ActivityMethod
    ReturnResult completeReceiving(String returnKey, String clientCode, String warehouseCode);

    // ========== Return Inspection ==========

    @ActivityMethod
    ReturnResult startInspection(String returnKey, String clientCode, String warehouseCode);

    @ActivityMethod
    ReturnLineResult inspectLineItem(InspectLineInput input, String clientCode, String warehouseCode);

    @ActivityMethod
    ReturnLineResult assignDisposition(AssignDispositionInput input, String clientCode, String warehouseCode);

    @ActivityMethod
    ReturnLineResult autoAssignDisposition(String returnKey, String sku, String clientCode, String warehouseCode);

    @ActivityMethod
    ReturnResult completeInspection(String returnKey, String clientCode, String warehouseCode);

    // ========== Return Processing ==========

    @ActivityMethod
    ReturnResult closeReturn(String returnKey, String clientCode, String warehouseCode);

    @ActivityMethod
    RefundResult calculateRefund(String returnKey, String clientCode, String warehouseCode);

    @ActivityMethod
    InventoryResult processInventoryUpdates(String returnKey, String clientCode, String warehouseCode);

    @ActivityMethod
    CreditMemoResult generateCreditMemo(String returnKey, String clientCode, String warehouseCode);

    @ActivityMethod
    ReturnResult cancelReturn(String returnKey, String reason, String clientCode, String warehouseCode);

    // ========== Queries ==========

    @ActivityMethod
    List<ReturnSummaryResult> getPendingReturns(String facility, String clientCode, String warehouseCode);

    @ActivityMethod
    List<ReturnSummaryResult> getReturnsRequiringInspection(String facility, String clientCode, String warehouseCode);

    @ActivityMethod
    ReturnSummaryResult getReturnSummary(String returnKey, String clientCode, String warehouseCode);

    // ========== Input DTOs ==========

    @Data
    @Builder
    class CreateReturnInput {
        private String storerKey;
        private String returnType;
        private String originalOrderKey;
        private String originalOrderNumber;
        private String rmaNumber;
        private LocalDateTime rmaDate;
        private LocalDateTime rmaExpiryDate;
        private String returnReasonCode;
        private String customerKey;
        private String customerName;
        private String customerEmail;
        private String shipFromName;
        private String shipFromAddress1;
        private String shipFromCity;
        private String shipFromState;
        private String shipFromZip;
        private String shipFromCountry;
        private String carrierCode;
        private String trackingNumber;
        private LocalDateTime expectedArrivalDate;
        private String notes;
    }

    @Data
    @Builder
    class ReceiveLineInput {
        private String returnKey;
        private String sku;
        private BigDecimal quantity;
        private BigDecimal expectedQty;
        private String returnReasonCode;
        private String lot;
        private String toId;
        private String toLoc;
        private String serialNumber;
    }

    @Data
    @Builder
    class InspectLineInput {
        private String returnKey;
        private String sku;
        private String inspectionStatus;
        private String inspectionGrade;
        private String inspectionNotes;
        private BigDecimal acceptedQty;
        private BigDecimal rejectedQty;
        private BigDecimal damagedQty;
        private String defectCode;
    }

    @Data
    @Builder
    class AssignDispositionInput {
        private String returnKey;
        private String sku;
        private String disposition;
        private String dispositionLocation;
        private String notes;
    }

    // ========== Result DTOs ==========

    @Data
    @Builder
    class ReturnResult {
        private boolean success;
        private String returnKey;
        private String rmaNumber;
        private String status;
        private String returnType;
        private String originalOrderKey;
        private BigDecimal totalExpected;
        private BigDecimal totalReceived;
        private List<String> errors;
    }

    @Data
    @Builder
    class ReturnLineResult {
        private boolean success;
        private String returnKey;
        private String sku;
        private BigDecimal quantity;
        private String disposition;
        private String inspectionGrade;
        private List<String> errors;
    }

    @Data
    @Builder
    class RefundResult {
        private boolean success;
        private String returnKey;
        private BigDecimal grossRefund;
        private BigDecimal restockingFee;
        private BigDecimal netRefund;
        private String currency;
        private List<String> errors;
    }

    @Data
    @Builder
    class InventoryResult {
        private boolean success;
        private String returnKey;
        private BigDecimal restockedQty;
        private BigDecimal refurbishedQty;
        private BigDecimal disposedQty;
        private BigDecimal vendorReturnQty;
        private List<String> errors;
    }

    @Data
    @Builder
    class CreditMemoResult {
        private boolean success;
        private String returnKey;
        private String creditMemoNumber;
        private BigDecimal amount;
        private String currency;
        private List<String> errors;
    }

    @Data
    @Builder
    class ReturnSummaryResult {
        private String returnKey;
        private String rmaNumber;
        private String status;
        private String returnType;
        private String originalOrderKey;
        private String customerName;
        private BigDecimal totalExpected;
        private BigDecimal totalReceived;
        private BigDecimal totalAccepted;
        private BigDecimal restockQty;
        private BigDecimal refurbishQty;
        private BigDecimal disposeQty;
        private BigDecimal refundAmount;
        private int lineCount;
    }
}
