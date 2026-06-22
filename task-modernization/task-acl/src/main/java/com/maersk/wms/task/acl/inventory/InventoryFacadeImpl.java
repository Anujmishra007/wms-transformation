package com.maersk.wms.task.acl.inventory;

import com.maersk.wms.task.shared.kernel.identifiers.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of InventoryFacade.
 * Placeholder for actual service integration.
 */
@Component
public class InventoryFacadeImpl implements InventoryFacade {

    @Override
    public Optional<InventoryDetails> getInventoryByLpn(LpnKey lpn) {
        // TODO: Connect to inventory service
        return Optional.empty();
    }

    @Override
    public List<InventoryDetails> getInventoryAtLocation(LocationKey location) {
        // TODO: Connect to inventory service
        return Collections.emptyList();
    }

    @Override
    public Optional<LocationInfo> getLocationInfo(LocationKey location) {
        // TODO: Connect to inventory service
        return Optional.empty();
    }

    @Override
    public boolean isLocationAvailable(LocationKey location) {
        // TODO: Connect to inventory service
        return false;
    }

    @Override
    public void notifyTaskCompleted(String taskKey, LpnKey lpn, LocationKey location, BigDecimal quantity) {
        // TODO: Connect to inventory service - probably via event
    }
}
