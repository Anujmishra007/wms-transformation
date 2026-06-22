package com.maersk.wms.outbound.shared.kernel.identifiers;

import lombok.Value;

/**
 * Unique identifier for a PickHeader (allocation header).
 */
@Value(staticConstructor = "of")
public class PickHeaderKey {
    String value;

    public static PickHeaderKey generate() {
        return of(java.util.UUID.randomUUID().toString());
    }
}
