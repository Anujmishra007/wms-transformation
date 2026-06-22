package com.maersk.wms.inventory.domain;

/**
 * Adjustment status enumeration.
 */
public enum AdjustmentStatus {
    PENDING,
    PENDING_APPROVAL,
    APPROVED,
    APPLIED,
    REJECTED,
    CANCELLED
}
