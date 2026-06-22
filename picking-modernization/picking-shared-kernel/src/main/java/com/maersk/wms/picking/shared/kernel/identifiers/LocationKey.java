package com.maersk.wms.picking.shared.kernel.identifiers;

import lombok.Value;

/**
 * Unique identifier for a warehouse location.
 */
@Value(staticConstructor = "of")
public class LocationKey {
    String value;
}
