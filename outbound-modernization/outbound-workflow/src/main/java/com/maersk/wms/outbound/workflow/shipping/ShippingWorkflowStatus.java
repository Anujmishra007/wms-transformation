package com.maersk.wms.outbound.workflow.shipping;

/**
 * Status enumeration for shipping workflow.
 * Represents the progression through the shipping lifecycle.
 */
public enum ShippingWorkflowStatus {

    // Initialization
    INITIATED("Workflow initiated"),
    VALIDATING("Validating shipment data"),

    // MBOL phase
    CREATING_MBOL("Creating MBOL"),
    MBOL_CREATED("MBOL created"),
    POPULATING_MBOL("Populating MBOL from load plan"),

    // Carrier phase
    SELECTING_CARRIER("Selecting carrier"),
    RATE_SHOPPING("Rate shopping carriers"),
    CARRIER_SELECTED("Carrier selected"),
    CALCULATING_FREIGHT("Calculating freight"),

    // Label phase
    GENERATING_LABELS("Generating shipping labels"),
    LABELS_GENERATED("Labels generated"),
    PRINTING_LABELS("Printing labels"),
    LABELS_PRINTED("Labels printed"),

    // Manifest phase
    ADDING_TO_MANIFEST("Adding to manifest"),
    MANIFESTED("Added to manifest"),
    CLOSING_MANIFEST("Closing manifest"),
    MANIFEST_CLOSED("Manifest closed"),
    TRANSMITTING_MANIFEST("Transmitting manifest to carrier"),
    MANIFEST_TRANSMITTED("Manifest transmitted"),

    // Ship phase
    READY_TO_SHIP("Ready to ship"),
    CONFIRMING_SHIPMENT("Confirming shipment"),
    SHIPPED("Shipment confirmed"),

    // Pickup phase
    SCHEDULING_PICKUP("Scheduling pickup"),
    PICKUP_SCHEDULED("Pickup scheduled"),
    AWAITING_PICKUP("Awaiting carrier pickup"),
    PICKED_UP("Picked up by carrier"),

    // Terminal states
    COMPLETED("Workflow completed successfully"),
    FAILED("Workflow failed"),
    CANCELLED("Workflow cancelled"),

    // Special states
    ON_HOLD("Shipment on hold"),
    PENDING_ADDRESS_VALIDATION("Pending address validation"),
    PENDING_CARRIER_RESPONSE("Pending carrier API response");

    private final String description;

    ShippingWorkflowStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if workflow is in a terminal state.
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }

    /**
     * Check if workflow is in MBOL phase.
     */
    public boolean isMbolPhase() {
        return this == CREATING_MBOL || this == MBOL_CREATED || this == POPULATING_MBOL;
    }

    /**
     * Check if workflow is in carrier phase.
     */
    public boolean isCarrierPhase() {
        return this == SELECTING_CARRIER || this == RATE_SHOPPING ||
               this == CARRIER_SELECTED || this == CALCULATING_FREIGHT;
    }

    /**
     * Check if workflow is in label phase.
     */
    public boolean isLabelPhase() {
        return this == GENERATING_LABELS || this == LABELS_GENERATED ||
               this == PRINTING_LABELS || this == LABELS_PRINTED;
    }

    /**
     * Check if workflow is in manifest phase.
     */
    public boolean isManifestPhase() {
        return this == ADDING_TO_MANIFEST || this == MANIFESTED ||
               this == CLOSING_MANIFEST || this == MANIFEST_CLOSED ||
               this == TRANSMITTING_MANIFEST || this == MANIFEST_TRANSMITTED;
    }

    /**
     * Check if workflow is in ship phase.
     */
    public boolean isShipPhase() {
        return this == READY_TO_SHIP || this == CONFIRMING_SHIPMENT || this == SHIPPED;
    }

    /**
     * Check if signals are allowed in current state.
     */
    public boolean allowsSignals() {
        return !isTerminal() && this != ON_HOLD && this != PENDING_CARRIER_RESPONSE;
    }

    /**
     * Get progress percentage.
     */
    public int getProgressPercent() {
        return switch (this) {
            case INITIATED, VALIDATING -> 5;
            case CREATING_MBOL, POPULATING_MBOL -> 15;
            case MBOL_CREATED -> 20;
            case SELECTING_CARRIER, RATE_SHOPPING -> 25;
            case CARRIER_SELECTED, CALCULATING_FREIGHT -> 35;
            case GENERATING_LABELS -> 45;
            case LABELS_GENERATED, PRINTING_LABELS, LABELS_PRINTED -> 55;
            case ADDING_TO_MANIFEST, MANIFESTED -> 65;
            case CLOSING_MANIFEST, MANIFEST_CLOSED -> 75;
            case TRANSMITTING_MANIFEST, MANIFEST_TRANSMITTED -> 80;
            case READY_TO_SHIP -> 85;
            case CONFIRMING_SHIPMENT -> 90;
            case SHIPPED -> 95;
            case SCHEDULING_PICKUP, PICKUP_SCHEDULED, AWAITING_PICKUP, PICKED_UP -> 98;
            case COMPLETED -> 100;
            case FAILED, CANCELLED -> 0;
            default -> 50;
        };
    }
}
