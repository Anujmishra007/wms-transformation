package com.maersk.wms.picking.shared.kernel.identifiers;

import lombok.Value;

/**
 * Unique identifier for a Pick Detail line.
 */
@Value(staticConstructor = "of")
public class PickDetailKey {
    String value;

    public static PickDetailKey generate() {
        return of(java.util.UUID.randomUUID().toString());
    }
}
