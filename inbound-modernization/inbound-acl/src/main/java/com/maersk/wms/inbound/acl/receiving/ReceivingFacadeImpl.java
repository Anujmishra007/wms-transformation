package com.maersk.wms.inbound.acl.receiving;

import com.maersk.wms.inbound.shared.kernel.identifiers.LpnKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.ReceiptKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;
import com.maersk.wms.inbound.shared.kernel.valueobjects.Quantity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Local implementation of ReceivingFacade.
 *
 * Delegates to Receiving domain services.
 * Will be replaced by REST client when Receiving becomes a separate service.
 *
 * Legacy SP References:
 * - nsp_GetReceiptHeader
 * - nsp_GetReceiptDetail
 * - nsp_UpdateReceiptDetailStatus
 */
@Component
public class ReceivingFacadeImpl implements ReceivingFacade {

    // TODO: Inject ReceivingService when available
    // private final ReceivingService receivingService;

    @Override
    public Optional<ReceiptSummary> getReceiptSummary(ReceiptKey receiptKey) {
        // TODO: Implement with actual receiving service
        // Receipt receipt = receivingService.findByKey(receiptKey);
        // return Optional.of(translateToSummary(receipt));
        throw new UnsupportedOperationException("Not yet implemented - wire to ReceivingService");
    }

    @Override
    public List<LpnForPutaway> getLpnsReadyForPutaway(ReceiptKey receiptKey) {
        // TODO: Implement with actual receiving service
        // Returns LPNs that have been received but not yet put away
        throw new UnsupportedOperationException("Not yet implemented - wire to ReceivingService");
    }

    @Override
    public void markLineAsPutAway(ReceiptKey receiptKey, String lineNumber, LpnKey lpn, Quantity quantity) {
        // TODO: Implement with actual receiving service
        // Updates receipt detail status to indicate putaway complete
        throw new UnsupportedOperationException("Not yet implemented - wire to ReceivingService");
    }

    @Override
    public boolean isReceiptComplete(ReceiptKey receiptKey) {
        // TODO: Implement with actual receiving service
        throw new UnsupportedOperationException("Not yet implemented - wire to ReceivingService");
    }

    @Override
    public StorerKey getReceiptStorer(ReceiptKey receiptKey) {
        // TODO: Implement with actual receiving service
        throw new UnsupportedOperationException("Not yet implemented - wire to ReceivingService");
    }
}
