package com.maersk.wms.masterdata.shared.kernel.exceptions;

/**
 * Exception thrown when a dock is not found.
 */
public class DockNotFoundException extends MasterDataException {

    public DockNotFoundException(String dockKey) {
        super("DOCK_NOT_FOUND", "Dock not found: " + dockKey);
    }
}
