package com.maersk.wms.events.contracts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

/**
 * Base implementation of DomainEvent with common fields.
 * All concrete events should extend this class.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseDomainEvent implements DomainEvent {

    private UUID eventId;
    private String eventType;
    private Instant occurredAt;
    private String clientCode;
    private String countryCode;
    private String warehouseCode;
    private int schemaVersion;
    private String correlationId;

    /**
     * Initialize common event fields.
     */
    protected void initializeEventMetadata(String eventType, String clientCode,
                                           String countryCode, String warehouseCode,
                                           String correlationId) {
        this.eventId = UUID.randomUUID();
        this.eventType = eventType;
        this.occurredAt = Instant.now();
        this.clientCode = clientCode;
        this.countryCode = countryCode;
        this.warehouseCode = warehouseCode;
        this.schemaVersion = 1;
        this.correlationId = correlationId;
    }
}
