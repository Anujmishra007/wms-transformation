package com.maersk.wms.inventory.shared.kernel.exceptions;

import com.maersk.wms.inventory.shared.kernel.identifiers.LpnKey;

/**
 * Exception thrown for inventory nesting/hierarchy errors.
 */
public class NestingException extends InventoryException {

    public NestingException(LpnKey parentLpn, LpnKey childLpn, String reason) {
        super("INV_NESTING", String.format(
                "Cannot nest LPN %s under %s: %s", childLpn.value(), parentLpn.value(), reason));
    }

    public NestingException(String message) {
        super("INV_NESTING", message);
    }
}
