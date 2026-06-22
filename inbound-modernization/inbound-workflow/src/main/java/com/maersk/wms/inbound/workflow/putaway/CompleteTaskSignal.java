package com.maersk.wms.inbound.workflow.putaway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Signal to complete a putaway task.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteTaskSignal {
    private String taskKey;
    private String actualLocation;  // If different from suggested
    private String userId;
}
