package com.maersk.wms.inbound.shared.kernel.events;

/**
 * Enumeration of inbound bounded contexts.
 * Maps to the master plan's subdomain structure:
 * - DOCUMENT: inbound-service (PO, ASN, OSDs, GRN)
 * - OPERATIONS: inbound-operations-service (Receiving, Returns Receipt, Putaway to Location, Crossdocking)
 * - PUTAWAY: putaway-service (Strategies, Location Allocation, Algorithm, Crossdock Strategy)
 *
 * Part of Shared Kernel - used for event routing and context identification.
 */
public enum InboundBoundedContext {

    /**
     * Document Management bounded context (inbound-service).
     * Manages PO, ASN, OSD, GRN documents.
     */
    DOCUMENT("inbound-service", "Document Management"),

    /**
     * Inbound Operations bounded context (inbound-operations-service).
     * Manages receiving, returns receipt, putaway tasks, crossdock execution.
     */
    OPERATIONS("inbound-operations-service", "Inbound Operations"),

    /**
     * Putaway Strategy bounded context (putaway-service).
     * Manages strategies, location allocation, algorithms, crossdock determination.
     */
    PUTAWAY("putaway-service", "Putaway Strategy");

    private final String serviceName;
    private final String description;

    InboundBoundedContext(String serviceName, String description) {
        this.serviceName = serviceName;
        this.description = description;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get context by service name.
     */
    public static InboundBoundedContext fromServiceName(String serviceName) {
        for (InboundBoundedContext context : values()) {
            if (context.serviceName.equals(serviceName)) {
                return context;
            }
        }
        throw new IllegalArgumentException("Unknown service name: " + serviceName);
    }
}
