package com.maersk.wms.picking.shared.kernel.identifiers;

import lombok.Value;

/**
 * Unique identifier for a Wave (from Order Service).
 */
@Value(staticConstructor = "of")
public class WaveKey {
    String value;
}
