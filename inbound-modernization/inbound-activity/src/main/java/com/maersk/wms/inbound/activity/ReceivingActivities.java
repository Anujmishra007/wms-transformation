package com.maersk.wms.inbound.activity;

import com.maersk.wms.inbound.domain.Receipt;
import com.maersk.wms.inbound.domain.ReceiptDetail;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.util.List;

/**
 * Temporal activities for receiving operations.
 */
@ActivityInterface
public interface ReceivingActivities {

    /**
     * Create a new receipt.
     */
    @ActivityMethod
    Receipt createReceipt(Receipt receipt);

    /**
     * Receive inventory for a receipt line.
     */
    @ActivityMethod
    ReceiptDetail receiveInventory(String receiptKey, String lineNumber, ReceiptDetail detail);

    /**
     * Validate receipt line before receiving.
     */
    @ActivityMethod
    boolean validateReceiptLine(ReceiptDetail detail);

    /**
     * Create inventory in LOTxLOCxID.
     */
    @ActivityMethod
    void createInventory(ReceiptDetail detail);

    /**
     * Generate putaway tasks for received items.
     */
    @ActivityMethod
    void generatePutawayTasks(Receipt receipt);

    /**
     * Close the receipt.
     */
    @ActivityMethod
    Receipt closeReceipt(String receiptKey);

    /**
     * Notify external systems of receipt completion.
     */
    @ActivityMethod
    void notifyReceiptComplete(Receipt receipt);

    /**
     * Update ASN status after receiving.
     */
    @ActivityMethod
    void updateAsnStatus(String asnKey);

    /**
     * Update PO received quantities.
     */
    @ActivityMethod
    void updatePoReceivedQty(String poKey, List<ReceiptDetail> details);
}
