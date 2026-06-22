package com.maersk.wms.masterdata.shared.kernel.exceptions;

/**
 * Exception thrown when attempting to create duplicate master data.
 */
public class DuplicateMasterDataException extends MasterDataException {

    public DuplicateMasterDataException(String entityType, String key) {
        super("DUPLICATE_" + entityType.toUpperCase(),
              entityType + " already exists with key: " + key);
    }
}
