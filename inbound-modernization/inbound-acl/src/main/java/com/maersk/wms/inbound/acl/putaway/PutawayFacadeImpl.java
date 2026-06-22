package com.maersk.wms.inbound.acl.putaway;

import com.maersk.wms.inbound.shared.kernel.identifiers.ReceiptKey;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Local implementation of PutawayFacade.
 *
 * Delegates to Putaway domain services.
 * Will be replaced by REST client when Putaway becomes a separate service.
 *
 * Legacy SP References:
 * - nsp_DirectedPutaway (location suggestion)
 * - nsp_CreatePutawayTask
 * - nsp_GetPutawayTasks
 * - nsp_CompletePutaway
 */
@Component
public class PutawayFacadeImpl implements PutawayFacade {

    // TODO: Inject PutawayService and LocationAllocationService when available
    // private final PutawayService putawayService;
    // private final LocationAllocationService locationService;

    @Override
    public Optional<LocationSuggestion> suggestLocation(LocationRequest request) {
        // TODO: Implement with actual putaway service
        // Uses putaway strategy to find optimal location
        throw new UnsupportedOperationException("Not yet implemented - wire to PutawayService");
    }

    @Override
    public String createPutawayTask(CreatePutawayTaskRequest request) {
        // TODO: Implement with actual putaway service
        // Creates a putaway task and returns the task key
        throw new UnsupportedOperationException("Not yet implemented - wire to PutawayService");
    }

    @Override
    public PutawayStatus getPutawayStatus(ReceiptKey receiptKey) {
        // TODO: Implement with actual putaway service
        throw new UnsupportedOperationException("Not yet implemented - wire to PutawayService");
    }

    @Override
    public boolean isReceiptFullyPutAway(ReceiptKey receiptKey) {
        // TODO: Implement with actual putaway service
        throw new UnsupportedOperationException("Not yet implemented - wire to PutawayService");
    }

    @Override
    public List<PutawayTaskSummary> getPendingTasks(ReceiptKey receiptKey) {
        // TODO: Implement with actual putaway service
        throw new UnsupportedOperationException("Not yet implemented - wire to PutawayService");
    }
}
