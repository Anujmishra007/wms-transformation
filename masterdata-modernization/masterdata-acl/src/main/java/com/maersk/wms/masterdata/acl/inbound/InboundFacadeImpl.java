package com.maersk.wms.masterdata.acl.inbound;

import com.maersk.wms.masterdata.shared.kernel.identifiers.*;

import org.springframework.stereotype.Component;

/**
 * Implementation of InboundFacade.
 * Placeholder for actual inbound service integration.
 */
@Component
public class InboundFacadeImpl implements InboundFacade {

    @Override
    public boolean storerHasPendingAsns(StorerKey storerKey) {
        // TODO: Connect to inbound service
        return false;
    }

    @Override
    public boolean dockHasActiveReceiving(DockKey dockKey) {
        // TODO: Connect to inbound service
        return false;
    }

    @Override
    public int getPendingReceivingCount(DockKey dockKey) {
        // TODO: Connect to inbound service
        return 0;
    }

    @Override
    public boolean skuOnPendingAsn(SkuKey skuKey) {
        // TODO: Connect to inbound service
        return false;
    }
}
