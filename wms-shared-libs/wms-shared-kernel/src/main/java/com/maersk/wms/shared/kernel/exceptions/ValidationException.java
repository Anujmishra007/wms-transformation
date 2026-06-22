package com.maersk.wms.shared.kernel.exceptions;

import java.util.Collections;
import java.util.List;

/**
 * Exception thrown when validation fails.
 * Contains list of validation errors.
 */
public class ValidationException extends WmsException {

    private final List<ValidationError> errors;

    public ValidationException(String message) {
        super("VALIDATION_ERROR", "VALIDATION", message);
        this.errors = Collections.emptyList();
    }

    public ValidationException(String message, List<ValidationError> errors) {
        super("VALIDATION_ERROR", "VALIDATION", message);
        this.errors = errors != null ? errors : Collections.emptyList();
    }

    public ValidationException(List<ValidationError> errors) {
        super("VALIDATION_ERROR", "VALIDATION", "Validation failed with " + errors.size() + " error(s)");
        this.errors = errors != null ? errors : Collections.emptyList();
    }

    public List<ValidationError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    /**
     * Validation error detail.
     */
    public record ValidationError(
            String field,
            String code,
            String message,
            Object rejectedValue
    ) {
        public ValidationError(String field, String message) {
            this(field, "INVALID", message, null);
        }

        public ValidationError(String field, String code, String message) {
            this(field, code, message, null);
        }
    }
}
