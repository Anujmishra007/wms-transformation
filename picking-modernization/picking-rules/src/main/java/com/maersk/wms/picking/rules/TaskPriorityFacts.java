package com.maersk.wms.picking.rules;

import com.maersk.wms.picking.domain.PickTask;
import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * Facts object for task prioritization rules.
 */
@Data
@Builder
public class TaskPriorityFacts {
    private List<PickTask> tasks;
    private List<PickTask> prioritizedTasks;
    private String priorityScheme;
}
