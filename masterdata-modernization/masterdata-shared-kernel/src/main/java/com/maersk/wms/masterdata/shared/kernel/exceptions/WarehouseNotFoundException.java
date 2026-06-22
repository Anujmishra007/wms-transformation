package com.maersk.wms.masterdata.shared.kernel.exceptions;

/**
 * Exception thrown when a warehouse is not found.
 */
public class WarehouseNotFoundException extends MasterDataException {

    public WarehouseNotFoundException(String warehouseKey) {
        super("WAREHOUSE_NOT_FOUND", "Warehouse not found: " + warehouseKey);
    }
}
