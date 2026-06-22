package com.maersk.wms.outbound.shared.kernel.identifiers;

import lombok.Value;

/**
 * Unique identifier for a Wave.
 */
@Value(staticConstructor = "of")
public class WaveKey {
    String value;

    public static WaveKey generate() {
        return of(java.util.UUID.randomUUID().toString());
    }
}
