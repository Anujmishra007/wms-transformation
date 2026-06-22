package com.maersk.wms.inbound.service.operations_service.dto;

import lombok.Data;

@Data
public class CrossdockStats {
    private long pendingCount;
    private long allocatedCount;
    private long releasedCount;
    private long pickedCount;
    private long stagedCount;
    private long loadedCount;
    private long shippedCount;
    private long opportunisticCount;
    private long plannedCount;
}
