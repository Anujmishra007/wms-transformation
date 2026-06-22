package com.maersk.wms.outbound.shared.kernel.identifiers;

import lombok.Value;

/**
 * Unique identifier for a Storer (client/customer).
 */
@Value(staticConstructor = "of")
public class StorerKey {
    String value;
}
