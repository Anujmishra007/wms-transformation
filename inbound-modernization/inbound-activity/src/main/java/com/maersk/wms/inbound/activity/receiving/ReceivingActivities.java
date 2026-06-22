package com.maersk.wms.inbound.activity.receiving;

import com.maersk.wms.inbound.service.operations_service.dto.ReceiveResult;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Temporal Activities interface for Receiving subdomain.
 *
 * Activities handle the actual business logic execution.
 *
 * Legacy SP References:
 * - nsp_CreateReceipt
 * - nsp_ReceiveInventory
 * - lsp_FinalizeReceipt_Wrapper
 */
@ActivityInterface
public interface ReceivingActivities {

    /**
     * Create a new receipt.
     */
    @ActivityMethod
    String createReceipt(String storerKey, String poKey, String userId);

    /**
     * Create receipt from ASN.
     */
    @ActivityMethod
    String createReceiptFromAsn(String asnKey, String userId);

    /**
     * Start receiving on a receipt.
     */
    @ActivityMethod
    void startReceiving(String receiptKey, String userId);

    /**
     * Receive a line item.
     */
    @ActivityMethod
    ReceiveResult receiveLineItem(String receiptKey, ReceiveLineInput input);

    /**
     * Report damage on a line.
     */
    @ActivityMethod
    void reportDamage(String receiptKey, DamageReportInput input);

    /**
     * Validate the receipt.
     */
    @ActivityMethod
    void validateReceipt(String receiptKey);

    /**
     * Complete receiving.
     */
    @ActivityMethod
    void completeReceiving(String receiptKey, String userId);

    /**
     * Trigger putaway for received items.
     */
    @ActivityMethod
    void triggerPutaway(String receiptKey);

    /**
     * Cancel a receipt.
     */
    @ActivityMethod
    void cancelReceipt(String receiptKey, String reason);

    // Input DTOs
    @Data
    @Builder
    class ReceiveLineInput {
        private String lineNumber;
        private String sku;
        private String packKey;
        private String uom;
        private BigDecimal quantity;
        private String lpn;
        private String location;
        private String conditionCode;
        private String userId;
    }

    @Data
    @Builder
    class DamageReportInput {
        private String lineNumber;
        private String sku;
        private BigDecimal damagedQty;
        private String damageCode;
        private String notes;
        private String userId;
    }
}
