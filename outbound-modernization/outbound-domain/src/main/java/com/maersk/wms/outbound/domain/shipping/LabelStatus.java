package com.maersk.wms.outbound.domain.shipping;

/**
 * Label status enumeration.
 */
public enum LabelStatus {
    PENDING,
    GENERATED,
    PRINTED,
    VOIDED,
    EXPIRED,
    ERROR
}
