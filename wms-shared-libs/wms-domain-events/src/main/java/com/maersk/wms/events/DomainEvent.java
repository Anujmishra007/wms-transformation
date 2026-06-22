package com.maersk.wms.events;

import java.time.Instant;

/**
 * Base interface for all WMS domain events.
 *
 * @deprecated Use {@link com.maersk.wms.events.contracts.DomainEvent} from wms-event-contracts instead.
 *             This interface is retained for backwards compatibility only.
 */
@Deprecated(since = "1.0.0", forRemoval = true)
public interface DomainEvent {

    String getEventId();
    String getEventType();
    Instant getTimestamp();
    String getSource();
    String getCorrelationId();
}
