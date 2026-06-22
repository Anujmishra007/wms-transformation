package com.maersk.wms.picking.shared.kernel.identifiers;

import lombok.Value;

/**
 * Unique identifier for a User.
 */
@Value(staticConstructor = "of")
public class UserKey {
    String value;
}
