package com.maersk.wms.masterdata.shared.kernel.exceptions;

/**
 * Exception thrown when a location is not found.
 */
public class LocationNotFoundException extends MasterDataException {

    public LocationNotFoundException(String locationKey) {
        super("LOCATION_NOT_FOUND", "Location not found: " + locationKey);
    }
}
