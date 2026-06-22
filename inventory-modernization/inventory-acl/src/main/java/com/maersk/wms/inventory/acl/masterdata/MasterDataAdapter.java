package com.maersk.wms.inventory.acl.masterdata;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Adapter implementation for Master Data Service facade.
 * Communicates with master-data-service via REST/gRPC.
 */
@Component
public class MasterDataAdapter implements MasterDataFacade {

    private final WebClient masterDataClient;

    public MasterDataAdapter(WebClient.Builder webClientBuilder) {
        this.masterDataClient = webClientBuilder
                .baseUrl("${services.master-data.url:http://master-data-service}")
                .build();
    }

    // ═══════════════════════════════════════════════════════════════
    // SKU QUERIES
    // ═══════════════════════════════════════════════════════════════

    @Override
    public Optional<SkuInfo> getSkuInfo(SkuKey skuKey) {
        // TODO: Implement REST call to master-data-service
        // GET /api/v1/skus/{skuKey}
        return Optional.empty();
    }

    @Override
    public Optional<SkuInfo> getSkuByCode(String skuCode, StorerKey storerKey) {
        // TODO: Implement REST call
        // GET /api/v1/skus?code={skuCode}&storer={storerKey}
        return Optional.empty();
    }

    @Override
    public List<SkuInfo> getSkusForStorer(StorerKey storerKey) {
        // TODO: Implement REST call
        // GET /api/v1/storers/{storerKey}/skus
        return Collections.emptyList();
    }

    @Override
    public boolean skuExists(SkuKey skuKey) {
        // TODO: Implement REST call
        // HEAD /api/v1/skus/{skuKey}
        return false;
    }

    @Override
    public Optional<SkuPackInfo> getSkuPackInfo(SkuKey skuKey) {
        // TODO: Implement REST call
        // GET /api/v1/skus/{skuKey}/pack-config
        return Optional.empty();
    }

    // ═══════════════════════════════════════════════════════════════
    // LOCATION QUERIES
    // ═══════════════════════════════════════════════════════════════

    @Override
    public Optional<LocationInfo> getLocationInfo(LocationKey locationKey) {
        // TODO: Implement REST call
        // GET /api/v1/locations/{locationKey}
        return Optional.empty();
    }

    @Override
    public Optional<LocationInfo> getLocationByCode(String locationCode, WarehouseKey warehouseKey) {
        // TODO: Implement REST call
        // GET /api/v1/locations?code={locationCode}&warehouse={warehouseKey}
        return Optional.empty();
    }

    @Override
    public List<LocationInfo> getLocationsInZone(String zoneCode, WarehouseKey warehouseKey) {
        // TODO: Implement REST call
        // GET /api/v1/zones/{zoneCode}/locations?warehouse={warehouseKey}
        return Collections.emptyList();
    }

    @Override
    public boolean locationExists(LocationKey locationKey) {
        // TODO: Implement REST call
        // HEAD /api/v1/locations/{locationKey}
        return false;
    }

    @Override
    public boolean canAcceptInventory(LocationKey locationKey, SkuKey skuKey, Quantity quantity) {
        // TODO: Implement REST call
        // POST /api/v1/locations/{locationKey}/can-accept
        return false;
    }

    @Override
    public LocationCapacity getLocationCapacity(LocationKey locationKey) {
        // TODO: Implement REST call
        // GET /api/v1/locations/{locationKey}/capacity
        return new LocationCapacity(locationKey, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    // ═══════════════════════════════════════════════════════════════
    // STORER QUERIES
    // ═══════════════════════════════════════════════════════════════

    @Override
    public Optional<StorerInfo> getStorerInfo(StorerKey storerKey) {
        // TODO: Implement REST call
        // GET /api/v1/storers/{storerKey}
        return Optional.empty();
    }

    @Override
    public Optional<StorerInfo> getStorerByCode(String storerCode) {
        // TODO: Implement REST call
        // GET /api/v1/storers?code={storerCode}
        return Optional.empty();
    }

    @Override
    public boolean storerExists(StorerKey storerKey) {
        // TODO: Implement REST call
        // HEAD /api/v1/storers/{storerKey}
        return false;
    }

    @Override
    public Optional<StorerInventorySettings> getStorerInventorySettings(StorerKey storerKey) {
        // TODO: Implement REST call
        // GET /api/v1/storers/{storerKey}/inventory-settings
        return Optional.empty();
    }

    // ═══════════════════════════════════════════════════════════════
    // WAREHOUSE QUERIES
    // ═══════════════════════════════════════════════════════════════

    @Override
    public Optional<WarehouseInfo> getWarehouseInfo(WarehouseKey warehouseKey) {
        // TODO: Implement REST call
        // GET /api/v1/warehouses/{warehouseKey}
        return Optional.empty();
    }

    @Override
    public Optional<WarehouseInfo> getWarehouseByCode(String warehouseCode) {
        // TODO: Implement REST call
        // GET /api/v1/warehouses?code={warehouseCode}
        return Optional.empty();
    }

    @Override
    public Optional<WarehouseInventorySettings> getWarehouseInventorySettings(WarehouseKey warehouseKey) {
        // TODO: Implement REST call
        // GET /api/v1/warehouses/{warehouseKey}/inventory-settings
        return Optional.empty();
    }

    // ═══════════════════════════════════════════════════════════════
    // UOM QUERIES
    // ═══════════════════════════════════════════════════════════════

    @Override
    public double getUomConversionFactor(String fromUom, String toUom, SkuKey skuKey) {
        // TODO: Implement REST call
        // GET /api/v1/skus/{skuKey}/uom-conversion?from={fromUom}&to={toUom}
        return 1.0;
    }

    @Override
    public Quantity convertToBaseUom(Quantity quantity, SkuKey skuKey) {
        // TODO: Implement conversion logic
        return quantity;
    }

    @Override
    public List<String> getValidUoms(SkuKey skuKey) {
        // TODO: Implement REST call
        // GET /api/v1/skus/{skuKey}/uoms
        return Collections.emptyList();
    }

    // ═══════════════════════════════════════════════════════════════
    // LOTTABLE CONFIGURATION
    // ═══════════════════════════════════════════════════════════════

    @Override
    public Optional<LottableConfig> getLottableConfig(StorerKey storerKey) {
        // TODO: Implement REST call
        // GET /api/v1/storers/{storerKey}/lottable-config
        return Optional.empty();
    }

    @Override
    public List<String> getLottableLabels(StorerKey storerKey) {
        // TODO: Implement REST call
        // GET /api/v1/storers/{storerKey}/lottable-labels
        return Collections.emptyList();
    }
}
