package com.maersk.wms.picking.shared.kernel.identifiers;

import lombok.Value;

/**
 * Unique identifier for an RDT Device.
 */
@Value(staticConstructor = "of")
public class DeviceKey {
    String value;
}
