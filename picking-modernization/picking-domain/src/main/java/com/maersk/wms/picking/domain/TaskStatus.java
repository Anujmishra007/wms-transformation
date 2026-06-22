package com.maersk.wms.picking.domain;

/**
 * Task status enumeration - maps to legacy single-digit status codes.
 *
 * Legacy mapping (from PICKDETAIL.STATUS):
 * 0 = NEW        → CREATED
 * 1 = RELEASED   → RELEASED
 * 2 = ASSIGNED   → ASSIGNED
 * 3 = IN_PROGRESS → IN_PROGRESS
 * 5 = COMPLETED  → COMPLETED
 * 6 = CANCELLED  → CANCELLED
 * 7 = SHORT      → SHORT_PICK
 * 9 = HELD       → HELD
 */
public enum TaskStatus {
    /** Task created but not yet released for execution */
    CREATED(0, "New"),

    /** Task released and available for assignment */
    RELEASED(1, "Released"),

    /** Task assigned to operator */
    ASSIGNED(2, "Assigned"),

    /** Task execution in progress */
    IN_PROGRESS(3, "In Progress"),

    /** Task completed successfully */
    COMPLETED(5, "Completed"),

    /** Task cancelled */
    CANCELLED(6, "Cancelled"),

    /** Task completed with short pick */
    SHORT_PICK(7, "Short Pick"),

    /** Task on hold */
    HELD(9, "Held");

    private final int legacyCode;
    private final String displayName;

    TaskStatus(int legacyCode, String displayName) {
        this.legacyCode = legacyCode;
        this.displayName = displayName;
    }

    public int getLegacyCode() {
        return legacyCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Convert from legacy status code.
     */
    public static TaskStatus fromLegacyCode(int code) {
        for (TaskStatus status : values()) {
            if (status.legacyCode == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown legacy status code: " + code);
    }

    /**
     * Check if this status represents an active task.
     */
    public boolean isActive() {
        return this == ASSIGNED || this == IN_PROGRESS;
    }

    /**
     * Check if this status represents a terminal state.
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED || this == SHORT_PICK;
    }
}
