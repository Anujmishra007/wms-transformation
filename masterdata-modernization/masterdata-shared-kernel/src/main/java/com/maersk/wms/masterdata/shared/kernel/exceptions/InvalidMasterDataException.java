package com.maersk.wms.masterdata.shared.kernel.exceptions;

import java.util.List;

/**
 * Exception thrown when master data validation fails.
 */
public class InvalidMasterDataException extends MasterDataException {

    private final List<String> validationErrors;

    public InvalidMasterDataException(String entityType, List<String> errors) {
        super("INVALID_" + entityType.toUpperCase(),
              "Invalid " + entityType + ": " + String.join(", ", errors));
        this.validationErrors = errors;
    }

    public InvalidMasterDataException(String entityType, String error) {
        this(entityType, List.of(error));
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }
}
