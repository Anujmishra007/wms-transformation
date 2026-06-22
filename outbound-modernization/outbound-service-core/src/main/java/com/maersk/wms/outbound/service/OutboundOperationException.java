package com.maersk.wms.outbound.service;

/**
 * Exception for outbound operation failures.
 */
public class OutboundOperationException extends RuntimeException {

    private String errorCode;
    private String orderNumber;
    private String waveNumber;
    private String shipmentId;

    public OutboundOperationException(String message) {
        super(message);
    }

    public OutboundOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutboundOperationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public static OutboundOperationException orderNotFound(String orderNumber) {
        OutboundOperationException ex = new OutboundOperationException("Order not found: " + orderNumber, "ORDER_NOT_FOUND");
        ex.orderNumber = orderNumber;
        return ex;
    }

    public static OutboundOperationException waveNotFound(String waveNumber) {
        OutboundOperationException ex = new OutboundOperationException("Wave not found: " + waveNumber, "WAVE_NOT_FOUND");
        ex.waveNumber = waveNumber;
        return ex;
    }

    public static OutboundOperationException shipmentNotFound(String shipmentId) {
        OutboundOperationException ex = new OutboundOperationException("Shipment not found: " + shipmentId, "SHIPMENT_NOT_FOUND");
        ex.shipmentId = shipmentId;
        return ex;
    }

    public static OutboundOperationException invalidStatus(String entity, String currentStatus, String expectedStatus) {
        return new OutboundOperationException(
                String.format("%s has invalid status. Current: %s, Expected: %s", entity, currentStatus, expectedStatus),
                "INVALID_STATUS"
        );
    }

    public static OutboundOperationException allocationFailed(String orderNumber, String reason) {
        OutboundOperationException ex = new OutboundOperationException("Allocation failed for order " + orderNumber + ": " + reason, "ALLOCATION_FAILED");
        ex.orderNumber = orderNumber;
        return ex;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getWaveNumber() {
        return waveNumber;
    }

    public String getShipmentId() {
        return shipmentId;
    }
}
