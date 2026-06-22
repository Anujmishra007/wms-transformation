package com.maersk.wms.picking.domain.task_execution_service.model;

/**
 * Pick session status enumeration.
 */
public enum SessionStatus {
    ACTIVE("A", "Active"),
    PAUSED("P", "Paused"),
    ENDED("E", "Ended"),
    COMPLETED("C", "Completed"),
    ABANDONED("X", "Abandoned");

    private final String code;
    private final String description;

    SessionStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static SessionStatus fromCode(String code) {
        for (SessionStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown session status code: " + code);
    }
}
