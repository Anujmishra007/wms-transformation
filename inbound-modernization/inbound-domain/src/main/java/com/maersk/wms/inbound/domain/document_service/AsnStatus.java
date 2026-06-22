package com.maersk.wms.inbound.domain.document_service;

/**
 * Status progression for ASN.
 */
public enum AsnStatus {
    DRAFT("D", "Draft", "ASN in draft"),
    OPEN("0", "Open", "ASN created/received"),
    SCHEDULED("1", "Scheduled", "Dock appointment scheduled"),
    IN_TRANSIT("2", "In Transit", "Shipment in transit"),
    ARRIVED("3", "Arrived", "Arrived at dock"),
    RECEIVING("4", "Receiving", "Being received"),
    RECEIVED("5", "Received", "Fully received"),
    CLOSED("9", "Closed", "ASN closed"),
    CANCELLED("X", "Cancelled", "ASN cancelled");

    private final String code;
    private final String label;
    private final String description;

    AsnStatus(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public static AsnStatus fromCode(String code) {
        for (AsnStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown ASN status code: " + code);
    }

    public boolean canReceive() {
        return this == OPEN || this == SCHEDULED || this == IN_TRANSIT || this == ARRIVED;
    }
}
