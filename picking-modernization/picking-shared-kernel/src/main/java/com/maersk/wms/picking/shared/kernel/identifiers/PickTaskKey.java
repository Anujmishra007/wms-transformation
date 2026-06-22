package com.maersk.wms.picking.shared.kernel.identifiers;

import lombok.Value;

/**
 * Unique identifier for a Pick Task.
 */
@Value(staticConstructor = "of")
public class PickTaskKey {
    String value;

    public static PickTaskKey generate() {
        return of(java.util.UUID.randomUUID().toString());
    }
}
