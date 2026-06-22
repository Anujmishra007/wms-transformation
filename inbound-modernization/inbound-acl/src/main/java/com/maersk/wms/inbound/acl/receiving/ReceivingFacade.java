package com.maersk.wms.inbound.acl.receiving;

import com.maersk.wms.inbound.shared.kernel.identifiers.LpnKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.ReceiptKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;
import com.maersk.wms.inbound.shared.kernel.valueobjects.Quantity;

import java.util.List;
import java.util.Optional;

/**
 * Facade interface for Receiving subdomain.
 *
 * Used by other subdomains (Returns, Putaway) to interact with Receiving
 * without direct coupling to Receiving's internal model.
 *
 * When Receiving is extracted to a separate microservice, this interface
 * will be implemented by a REST client instead of local calls.
 */
public interface ReceivingFacade {

    /**
     * Get receipt summary for putaway processing.
     */
    Optional<ReceiptSummary> getReceiptSummary(ReceiptKey receiptKey);

    /**
     * Get all LPNs from a receipt that are ready for putaway.
     */
    List<LpnForPutaway> getLpnsReadyForPutaway(ReceiptKey receiptKey);

    /**
     * Mark receipt line as put away.
     * Called by Putaway subdomain after successful putaway.
     */
    void markLineAsPutAway(ReceiptKey receiptKey, String lineNumber, LpnKey lpn, Quantity quantity);

    /**
     * Check if receipt is complete (all lines received and put away).
     */
    boolean isReceiptComplete(ReceiptKey receiptKey);

    /**
     * Get storer for a receipt (needed for putaway location determination).
     */
    StorerKey getReceiptStorer(ReceiptKey receiptKey);

    /**
     * Summary of a receipt for cross-subdomain use.
     */
    record ReceiptSummary(
            ReceiptKey receiptKey,
            String receiptType,
            StorerKey storerKey,
            String status,
            int totalLines,
            int receivedLines,
            int putawayLines
    ) {}

    /**
     * LPN data needed for putaway processing.
     */
    record LpnForPutaway(
            LpnKey lpn,
            String sku,
            Quantity quantity,
            String packKey,
            String lotAttributes,
            String sourceLocation
    ) {}
}
