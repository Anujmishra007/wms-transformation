package com.maersk.wms.printing.shared.kernel.exceptions;

import java.util.List;

/**
 * Exception thrown when label data validation fails.
 */
public class InvalidLabelDataException extends PrintingException {

    private final List<String> validationErrors;

    public InvalidLabelDataException(String labelType, List<String> errors) {
        super("INVALID_LABEL_DATA",
              "Invalid label data for " + labelType + ": " + String.join(", ", errors));
        this.validationErrors = errors;
    }

    public InvalidLabelDataException(String labelType, String error) {
        this(labelType, List.of(error));
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }
}
