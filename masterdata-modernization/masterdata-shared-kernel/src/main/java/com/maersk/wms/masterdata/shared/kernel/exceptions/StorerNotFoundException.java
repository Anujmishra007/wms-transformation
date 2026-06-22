package com.maersk.wms.masterdata.shared.kernel.exceptions;

/**
 * Exception thrown when a storer (customer) is not found.
 */
public class StorerNotFoundException extends MasterDataException {

    public StorerNotFoundException(String storerKey) {
        super("STORER_NOT_FOUND", "Storer not found: " + storerKey);
    }
}
