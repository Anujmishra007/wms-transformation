package com.maersk.wms.picking.shared.kernel.identifiers;

import lombok.Value;

/**
 * Unique identifier for an Order (from Order Service).
 */
@Value(staticConstructor = "of")
public class OrderKey {
    String value;
}
