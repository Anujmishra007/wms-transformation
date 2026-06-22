package com.maersk.wms.inbound.activity.receiving;

import com.maersk.wms.inbound.service.receiving.dto.ReceiveResult;
import com.maersk.wms.inbound.workflow.receiving.DamageReportSignal;
import com.maersk.wms.inbound.workflow.receiving.ReceiveLineSignal;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

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
    ReceiveResult receiveLineItem(String receiptKey, ReceiveLineSignal signal);

    /**
     * Report damage on a line.
     */
    @ActivityMethod
    void reportDamage(String receiptKey, DamageReportSignal signal);

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
}
