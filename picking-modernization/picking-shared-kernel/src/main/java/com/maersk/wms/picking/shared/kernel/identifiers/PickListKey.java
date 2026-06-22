package com.maersk.wms.picking.shared.kernel.identifiers;

import lombok.Value;

/**
 * Unique identifier for a Pick List.
 */
@Value(staticConstructor = "of")
public class PickListKey {
    String value;

    public static PickListKey generate() {
        return of(java.util.UUID.randomUUID().toString());
    }
}
