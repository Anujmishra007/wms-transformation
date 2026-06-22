package com.maersk.wms.inbound.acl.returns;

import com.maersk.wms.inbound.shared.kernel.identifiers.LpnKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.ReceiptKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;
import com.maersk.wms.inbound.shared.kernel.valueobjects.Quantity;

import java.util.List;
import java.util.Optional;

/**
 * Facade interface for Returns subdomain.
 *
 * Used by other subdomains to interact with Returns processing
 * without direct coupling to Returns' internal model.
 *
 * Returns are a special type of receipt (RECType='RETURN') that
 * require inspection and disposition before putaway.
 */
public interface ReturnsFacade {

    /**
     * Get return summary for a receipt key.
     * Returns are linked to receipts with type 'RETURN'.
     */
    Optional<ReturnSummary> getReturnSummary(ReceiptKey receiptKey);

    /**
     * Get items ready for return putaway (inspected + dispositioned).
     */
    List<ReturnItemForPutaway> getItemsReadyForPutaway(String returnKey);

    /**
     * Mark return item as put away.
     */
    void markItemAsPutAway(String returnKey, String lineNumber, LpnKey lpn);

    /**
     * Check if return has any items requiring special disposition (e.g., destroy, refurbish).
     */
    boolean hasSpecialDispositionItems(String returnKey);

    /**
     * Get disposition type for a return line.
     */
    String getDisposition(String returnKey, String lineNumber);

    /**
     * Summary of a return for cross-subdomain use.
     */
    record ReturnSummary(
            String returnKey,
            ReceiptKey linkedReceiptKey,
            String rmaNumber,
            StorerKey storerKey,
            String status,
            int totalLines,
            int inspectedLines,
            int dispositionedLines,
            int putawayLines
    ) {}

    /**
     * Return item data needed for putaway.
     */
    record ReturnItemForPutaway(
            LpnKey lpn,
            String sku,
            Quantity quantity,
            String disposition,
            String conditionCode,
            String targetZone  // Based on disposition
    ) {}
}
