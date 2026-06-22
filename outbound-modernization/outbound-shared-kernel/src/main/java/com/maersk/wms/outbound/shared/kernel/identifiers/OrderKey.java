package com.maersk.wms.outbound.shared.kernel.identifiers;

import lombok.Value;

/**
 * Unique identifier for an Order.
 */
@Value(staticConstructor = "of")
public class OrderKey {
    String value;

    public static OrderKey generate() {
        return of(java.util.UUID.randomUUID().toString());
    }
}
