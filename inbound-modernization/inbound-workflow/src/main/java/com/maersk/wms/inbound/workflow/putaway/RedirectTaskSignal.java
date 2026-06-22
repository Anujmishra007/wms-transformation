package com.maersk.wms.inbound.workflow.putaway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Signal to redirect a task to a different location.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedirectTaskSignal {
    private String taskKey;
    private String newLocation;
    private String reason;
    private String userId;
}
