package com.maersk.wms.events.publishing;

import com.maersk.wms.events.contracts.DomainEvent;

/**
 * Resolves the Kafka topic for a given domain event.
 * Default implementation uses event type naming conventions.
 */
public interface EventTopicResolver {

    /**
     * Resolve the topic name for the given event.
     *
     * @param event The domain event
     * @return The Kafka topic name
     */
    String resolveTopic(DomainEvent event);

    /**
     * Default topic resolver using convention:
     * wms.{domain}.{event-type}
     * e.g., wms.inventory.inventory-adjusted
     */
    static EventTopicResolver defaultResolver() {
        return event -> {
            String eventType = event.getEventType();
            String domain = extractDomain(eventType);
            String normalizedType = toKebabCase(eventType);
            return String.format("wms.%s.%s", domain, normalizedType);
        };
    }

    private static String extractDomain(String eventType) {
        // Extract domain from event class package or naming convention
        // e.g., InventoryAdjusted -> inventory
        //       OrderCreated -> order
        //       PickConfirmed -> picking
        if (eventType.startsWith("Inventory")) return "inventory";
        if (eventType.startsWith("Order")) return "order";
        if (eventType.startsWith("Pick")) return "picking";
        if (eventType.startsWith("Pack")) return "packing";
        if (eventType.startsWith("Receipt") || eventType.startsWith("Putaway")) return "inbound";
        if (eventType.startsWith("Task")) return "task";
        if (eventType.startsWith("Sku") || eventType.startsWith("Location") || eventType.startsWith("Storer")) return "masterdata";
        return "wms"; // fallback
    }

    private static String toKebabCase(String input) {
        return input.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
    }
}
