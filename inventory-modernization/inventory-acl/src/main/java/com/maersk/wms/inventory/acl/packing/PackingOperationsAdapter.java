package com.maersk.wms.inventory.acl.packing;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Adapter implementation for Packing Operations Service facade.
 * Communicates with packing-operations-service via REST/gRPC.
 */
@Component
public class PackingOperationsAdapter implements PackingOperationsFacade {

    private final WebClient packingClient;

    public PackingOperationsAdapter(WebClient.Builder webClientBuilder) {
        this.packingClient = webClientBuilder
                .baseUrl("${services.packing-operations.url:http://packing-operations-service}")
                .build();
    }

    // ═══════════════════════════════════════════════════════════════
    // PACK QUERIES
    // ═══════════════════════════════════════════════════════════════

    @Override
    public Optional<PackInfo> getPackInfo(String packKey) {
        // TODO: Implement REST call to packing-operations-service
        // GET /api/v1/packs/{packKey}
        return Optional.empty();
    }

    @Override
    public List<PackContentInfo> getPackContents(String packKey) {
        // TODO: Implement REST call
        // GET /api/v1/packs/{packKey}/contents
        return Collections.emptyList();
    }

    @Override
    public List<PackInfo> getPacksForOrder(OrderKey orderKey) {
        // TODO: Implement REST call
        // GET /api/v1/orders/{orderKey}/packs
        return Collections.emptyList();
    }

    @Override
    public boolean isPackComplete(String packKey) {
        // TODO: Implement REST call
        // GET /api/v1/packs/{packKey}/status
        return false;
    }

    // ═══════════════════════════════════════════════════════════════
    // CARTON QUERIES
    // ═══════════════════════════════════════════════════════════════

    @Override
    public Optional<CartonInfo> getCartonInfo(LpnKey cartonLpn) {
        // TODO: Implement REST call
        // GET /api/v1/cartons/{cartonLpn}
        return Optional.empty();
    }

    @Override
    public List<CartonContentInfo> getCartonContents(LpnKey cartonLpn) {
        // TODO: Implement REST call
        // GET /api/v1/cartons/{cartonLpn}/contents
        return Collections.emptyList();
    }

    @Override
    public List<LpnKey> getCartonsOnPallet(LpnKey palletLpn) {
        // TODO: Implement REST call
        // GET /api/v1/pallets/{palletLpn}/cartons
        return Collections.emptyList();
    }

    // ═══════════════════════════════════════════════════════════════
    // INVENTORY CONSUMPTION
    // ═══════════════════════════════════════════════════════════════

    @Override
    public List<InventoryConsumption> getInventoryConsumedByPack(String packKey) {
        // TODO: Implement REST call
        // GET /api/v1/packs/{packKey}/inventory-consumption
        return Collections.emptyList();
    }

    @Override
    public List<InventoryKey> getSourceInventoryForCarton(LpnKey cartonLpn) {
        // TODO: Implement REST call
        // GET /api/v1/cartons/{cartonLpn}/source-inventory
        return Collections.emptyList();
    }

    // ═══════════════════════════════════════════════════════════════
    // NOTIFICATION CALLBACKS
    // ═══════════════════════════════════════════════════════════════

    @Override
    public void notifyInventoryMovedToCarton(LpnKey cartonLpn, InventoryKey inventoryKey, Quantity quantity) {
        // TODO: Implement REST call or event publish
        // POST /api/v1/cartons/{cartonLpn}/inventory-moved
        // OR publish InventoryMovedToCartonEvent to Kafka
    }

    @Override
    public void notifyCartonReadyForShip(LpnKey cartonLpn) {
        // TODO: Implement REST call or event publish
        // POST /api/v1/cartons/{cartonLpn}/ready-for-ship
        // OR publish CartonReadyForShipEvent to Kafka
    }
}
