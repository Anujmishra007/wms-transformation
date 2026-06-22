package com.maersk.wms.events.publishing;

/**
 * Exception thrown when event publishing fails.
 */
public class EventPublishException extends RuntimeException {

    private final String eventType;
    private final String eventId;

    public EventPublishException(String eventType, String eventId, String message) {
        super(String.format("Failed to publish event [type=%s, id=%s]: %s", eventType, eventId, message));
        this.eventType = eventType;
        this.eventId = eventId;
    }

    public EventPublishException(String eventType, String eventId, String message, Throwable cause) {
        super(String.format("Failed to publish event [type=%s, id=%s]: %s", eventType, eventId, message), cause);
        this.eventType = eventType;
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getEventId() {
        return eventId;
    }
}
