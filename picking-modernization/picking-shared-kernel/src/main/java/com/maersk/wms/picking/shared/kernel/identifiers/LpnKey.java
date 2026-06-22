package com.maersk.wms.picking.shared.kernel.identifiers;

import lombok.Value;

/**
 * Unique identifier for a License Plate Number (LPN/ID).
 */
@Value(staticConstructor = "of")
public class LpnKey {
    String value;

    public static LpnKey generate() {
        return of(java.util.UUID.randomUUID().toString());
    }
}
