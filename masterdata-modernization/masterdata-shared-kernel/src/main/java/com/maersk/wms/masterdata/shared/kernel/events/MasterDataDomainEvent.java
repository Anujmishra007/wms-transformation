package com.maersk.wms.masterdata.shared.kernel.events;

import java.time.Instant;

/**
 * Base interface for all Master Data domain events.
 */
public interface MasterDataDomainEvent {

    String aggregateId();

    String eventType();

    Instant occurredAt();

    default String boundedContext() {
        return "MASTER_DATA";
    }
}
