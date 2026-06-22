package com.maersk.wms.inbound.shared.kernel.exceptions;

import com.maersk.wms.inbound.shared.kernel.events.InboundBoundedContext;

/**
 * Base exception for all inbound domain exceptions.
 * Provides context about which bounded context the error originated from.
 *
 * Part of Shared Kernel - extend this for specific domain exceptions.
 */
public class InboundException extends RuntimeException {

    private final InboundBoundedContext sourceContext;
    private final String errorCode;

    public InboundException(String message, InboundBoundedContext sourceContext) {
        super(message);
        this.sourceContext = sourceContext;
        this.errorCode = "INBOUND_ERROR";
    }

    public InboundException(String message, InboundBoundedContext sourceContext, String errorCode) {
        super(message);
        this.sourceContext = sourceContext;
        this.errorCode = errorCode;
    }

    public InboundException(String message, Throwable cause, InboundBoundedContext sourceContext) {
        super(message, cause);
        this.sourceContext = sourceContext;
        this.errorCode = "INBOUND_ERROR";
    }

    public InboundException(String message, Throwable cause, InboundBoundedContext sourceContext, String errorCode) {
        super(message, cause);
        this.sourceContext = sourceContext;
        this.errorCode = errorCode;
    }

    public InboundBoundedContext getSourceContext() {
        return sourceContext;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
               "message='" + getMessage() + '\'' +
               ", errorCode='" + errorCode + '\'' +
               ", sourceContext=" + sourceContext +
               '}';
    }
}
