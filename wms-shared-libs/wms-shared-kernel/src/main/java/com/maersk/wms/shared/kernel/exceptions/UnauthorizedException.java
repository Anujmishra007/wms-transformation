package com.maersk.wms.shared.kernel.exceptions;

/**
 * Exception thrown when a user is not authorized.
 * Common across all microservices for security.
 */
public class UnauthorizedException extends WmsException {

    private final String userId;
    private final String resource;
    private final String action;

    public UnauthorizedException(String message) {
        super("UNAUTHORIZED", "SECURITY", message);
        this.userId = null;
        this.resource = null;
        this.action = null;
    }

    public UnauthorizedException(String userId, String resource, String action) {
        super("UNAUTHORIZED", "SECURITY",
                "User " + userId + " is not authorized to " + action + " on " + resource);
        this.userId = userId;
        this.resource = resource;
        this.action = action;
    }

    public String getUserId() {
        return userId;
    }

    public String getResource() {
        return resource;
    }

    public String getAction() {
        return action;
    }
}
