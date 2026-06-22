package com.maersk.wms.masterdata.acl.inbound;

import com.maersk.wms.masterdata.shared.kernel.identifiers.*;

/**
 * ACL facade for Inbound Service communication.
 * Provides master data queries needed by receiving operations.
 */
public interface InboundFacade {

    /**
     * Check if storer has pending ASNs.
     */
    boolean storerHasPendingAsns(StorerKey storerKey);

    /**
     * Check if dock is assigned to receiving.
     */
    boolean dockHasActiveReceiving(DockKey dockKey);

    /**
     * Get pending receiving count for dock.
     */
    int getPendingReceivingCount(DockKey dockKey);

    /**
     * Check if SKU is on any pending ASN.
     */
    boolean skuOnPendingAsn(SkuKey skuKey);
}
