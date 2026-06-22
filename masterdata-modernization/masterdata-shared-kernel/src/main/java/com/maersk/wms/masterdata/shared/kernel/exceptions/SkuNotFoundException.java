package com.maersk.wms.masterdata.shared.kernel.exceptions;

/**
 * Exception thrown when a SKU is not found.
 */
public class SkuNotFoundException extends MasterDataException {

    public SkuNotFoundException(String skuKey) {
        super("SKU_NOT_FOUND", "SKU not found: " + skuKey);
    }
}
