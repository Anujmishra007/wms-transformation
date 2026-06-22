package com.maersk.wms.inbound.service.operations_service.dto;

import lombok.Data;

@Data
public class PutawayTaskStats {
    private String zone;
    private long pendingCount;
    private long inProgressCount;
    private long completedCount;
}
