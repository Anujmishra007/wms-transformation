package com.maersk.wms.inbound.shared.kernel.exceptions;

import com.maersk.wms.inbound.shared.kernel.events.InboundBoundedContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exception thrown when domain validation fails.
 * Can contain multiple validation errors.
 *
 * Part of Shared Kernel - used for input validation across bounded contexts.
 */
public class ValidationException extends InboundException {

    private final List<ValidationError> errors;

    public ValidationException(String message, InboundBoundedContext sourceContext) {
        super(message, sourceContext, "VALIDATION_ERROR");
        this.errors = new ArrayList<>();
    }

    public ValidationException(String message, InboundBoundedContext sourceContext, List<ValidationError> errors) {
        super(message, sourceContext, "VALIDATION_ERROR");
        this.errors = new ArrayList<>(errors);
    }

    public ValidationException(String field, String message, InboundBoundedContext sourceContext) {
        super(message, sourceContext, "VALIDATION_ERROR");
        this.errors = new ArrayList<>();
        this.errors.add(new ValidationError(field, message));
    }

    public List<ValidationError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public void addError(String field, String message) {
        errors.add(new ValidationError(field, message));
    }

    public void addError(ValidationError error) {
        errors.add(error);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Represents a single validation error.
     */
    public record ValidationError(String field, String message, String code) {
        public ValidationError(String field, String message) {
            this(field, message, null);
        }
    }
}
