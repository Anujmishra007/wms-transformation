package com.maersk.wms.picking.kafka;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class TaskAssignedEvent {
    private String taskId;
    private String userId;
    private String warehouse;
    private String zone;
    @Builder.Default
    private Instant timestamp = Instant.now();
}
