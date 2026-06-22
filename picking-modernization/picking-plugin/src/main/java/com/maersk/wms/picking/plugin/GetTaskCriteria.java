package com.maersk.wms.picking.plugin;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * Criteria for task retrieval - can be modified by GetTaskPlugin.
 */
@Data
@Builder
public class GetTaskCriteria {

    /** Zones to filter by */
    private List<String> zones;

    /** Aisles to filter by */
    private List<String> aisles;

    /** Task types to include */
    private List<String> taskTypes;

    /** Priority range */
    private Integer minPriority;
    private Integer maxPriority;

    /** Order types to include */
    private List<String> orderTypes;

    /** Whether to include assigned tasks */
    private boolean includeAssigned;

    /** Maximum number of tasks to return */
    private int maxTasks;

    /** Sort order (e.g., "PRIORITY_DESC", "LOCATION_ASC") */
    private String sortOrder;

    /** Wave ID filter */
    private String waveId;

    /** Client-specific filter criteria */
    private String customFilter;
}
