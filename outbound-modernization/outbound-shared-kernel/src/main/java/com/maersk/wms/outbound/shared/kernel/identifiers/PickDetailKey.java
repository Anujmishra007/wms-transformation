package com.maersk.wms.outbound.shared.kernel.identifiers;

import lombok.Value;

/**
 * Unique identifier for a PickDetail line.
 */
@Value(staticConstructor = "of")
public class PickDetailKey {
    String value;

    public static PickDetailKey generate() {
        return of(java.util.UUID.randomUUID().toString());
    }
}
