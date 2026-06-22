package com.maersk.wms.task.rules.facts;

import com.maersk.wms.task.domain.enums.TaskType;
import com.maersk.wms.task.domain.enums.TaskPriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Facts object for task assignment rules evaluation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssignmentFacts {

    private String taskId;
    private TaskType taskType;
    private TaskPriority priority;

    // Location context
    private String sourceLocation;
    private String sourceZone;
    private String sourceAisle;
    private String destinationLocation;
    private String destinationZone;

    // Task requirements
    private Double quantity;
    private Double weight;
    private String equipmentRequired;
    private List<String> skillsRequired;
    private Boolean hazmatCertificationRequired;
    private Boolean forkliftCertificationRequired;

    // User being evaluated
    private String userId;
    private String userCurrentLocation;
    private String userCurrentZone;
    private String userWorkGroup;
    private List<String> userSkills;
    private Boolean userHasHazmatCertification;
    private Boolean userHasForkliftCertification;
    private Integer userCurrentTaskCount;
    private Integer userMaxTasks;
    private Double userProductivityRate;

    // Client/Facility
    private String clientCode;
    private String facilityCode;
}
