package com.maersk.wms.inbound.acl.returns;

import com.maersk.wms.inbound.shared.kernel.identifiers.LpnKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.ReceiptKey;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Local implementation of ReturnsFacade.
 *
 * Delegates to Returns domain services.
 * Will be replaced by REST client when Returns becomes a separate service.
 *
 * Legacy SP References:
 * - nsp_GetReturnHeader (via RECEIPT with RECTYPE='RETURN')
 * - nsp_GetReturnDetail
 * - nsp_ProcessReturnDisposition
 */
@Component
public class ReturnsFacadeImpl implements ReturnsFacade {

    // TODO: Inject ReturnService when available
    // private final ReturnReceivingService returnService;

    @Override
    public Optional<ReturnSummary> getReturnSummary(ReceiptKey receiptKey) {
        // TODO: Implement with actual return service
        throw new UnsupportedOperationException("Not yet implemented - wire to ReturnService");
    }

    @Override
    public List<ReturnItemForPutaway> getItemsReadyForPutaway(String returnKey) {
        // TODO: Implement with actual return service
        // Returns items that have been inspected and have a disposition assigned
        throw new UnsupportedOperationException("Not yet implemented - wire to ReturnService");
    }

    @Override
    public void markItemAsPutAway(String returnKey, String lineNumber, LpnKey lpn) {
        // TODO: Implement with actual return service
        throw new UnsupportedOperationException("Not yet implemented - wire to ReturnService");
    }

    @Override
    public boolean hasSpecialDispositionItems(String returnKey) {
        // TODO: Implement with actual return service
        // Check for dispositions like DESTROY, REFURBISH, RETURN_TO_VENDOR
        throw new UnsupportedOperationException("Not yet implemented - wire to ReturnService");
    }

    @Override
    public String getDisposition(String returnKey, String lineNumber) {
        // TODO: Implement with actual return service
        throw new UnsupportedOperationException("Not yet implemented - wire to ReturnService");
    }
}
