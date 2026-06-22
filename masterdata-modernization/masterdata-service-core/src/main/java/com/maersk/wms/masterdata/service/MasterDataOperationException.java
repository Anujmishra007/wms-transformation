package com.maersk.wms.masterdata.service;

/**
 * Exception for master data operation failures.
 */
public class MasterDataOperationException extends RuntimeException {

    private String errorCode;
    private String entityType;
    private String entityCode;

    public MasterDataOperationException(String message) {
        super(message);
    }

    public MasterDataOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MasterDataOperationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public static MasterDataOperationException itemNotFound(String sku) {
        MasterDataOperationException ex = new MasterDataOperationException("Item not found: " + sku, "ITEM_NOT_FOUND");
        ex.entityType = "ITEM";
        ex.entityCode = sku;
        return ex;
    }

    public static MasterDataOperationException locationNotFound(String locationCode) {
        MasterDataOperationException ex = new MasterDataOperationException("Location not found: " + locationCode, "LOCATION_NOT_FOUND");
        ex.entityType = "LOCATION";
        ex.entityCode = locationCode;
        return ex;
    }

    public static MasterDataOperationException customerNotFound(String customerCode) {
        MasterDataOperationException ex = new MasterDataOperationException("Customer not found: " + customerCode, "CUSTOMER_NOT_FOUND");
        ex.entityType = "CUSTOMER";
        ex.entityCode = customerCode;
        return ex;
    }

    public static MasterDataOperationException carrierNotFound(String carrierCode) {
        MasterDataOperationException ex = new MasterDataOperationException("Carrier not found: " + carrierCode, "CARRIER_NOT_FOUND");
        ex.entityType = "CARRIER";
        ex.entityCode = carrierCode;
        return ex;
    }

    public static MasterDataOperationException duplicateEntity(String entityType, String entityCode) {
        MasterDataOperationException ex = new MasterDataOperationException(
                entityType + " already exists: " + entityCode, "DUPLICATE_ENTITY");
        ex.entityType = entityType;
        ex.entityCode = entityCode;
        return ex;
    }

    public static MasterDataOperationException validationFailed(String entityType, String errors) {
        return new MasterDataOperationException(
                entityType + " validation failed: " + errors, "VALIDATION_FAILED");
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityCode() {
        return entityCode;
    }
}
