package com.maersk.wms.masterdata.acl.inventory;

import com.maersk.wms.masterdata.shared.kernel.identifiers.*;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Implementation of InventoryFacade.
 * Placeholder for actual inventory service integration.
 */
@Component
public class InventoryFacadeImpl implements InventoryFacade {

    @Override
    public Optional<BigDecimal> getInventoryQuantity(SkuKey skuKey, LocationKey locationKey) {
        // TODO: Connect to inventory service
        return Optional.empty();
    }

    @Override
    public boolean hasInventory(LocationKey locationKey) {
        // TODO: Connect to inventory service
        return false;
    }

    @Override
    public BigDecimal getTotalInventoryBySku(SkuKey skuKey) {
        // TODO: Connect to inventory service
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalInventoryByStorer(StorerKey storerKey) {
        // TODO: Connect to inventory service
        return BigDecimal.ZERO;
    }

    @Override
    public boolean isSkuAllocated(SkuKey skuKey) {
        // TODO: Connect to inventory service
        return false;
    }

    @Override
    public boolean isLocationAllocated(LocationKey locationKey) {
        // TODO: Connect to inventory service
        return false;
    }
}
