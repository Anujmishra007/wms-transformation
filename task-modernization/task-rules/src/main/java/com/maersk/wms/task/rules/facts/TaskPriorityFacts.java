package com.maersk.wms.task.rules.facts;

import com.maersk.wms.task.domain.enums.TaskType;
import com.maersk.wms.task.domain.enums.TaskPriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Facts object for task priority rules evaluation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskPriorityFacts {

    private String taskId;
    private TaskType taskType;
    private TaskPriority currentPriority;

    // Order/Shipment context
    private String orderNumber;
    private String orderType;
    private String customerCode;
    private String carrierCode;
    private String serviceLevel;
    private LocalDateTime requiredShipDate;
    private LocalDateTime promisedDeliveryDate;

    // Task context
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
    private Integer ageMinutes;
    private String sourceZone;
    private String destinationZone;

    // Quantity context
    private Double quantity;
    private Double orderValue;

    // Flags
    private Boolean isHotOrder;
    private Boolean isVipCustomer;
    private Boolean isExpedited;
    private Boolean isBackorder;
    private Boolean isConsolidation;

    // Client/Facility
    private String clientCode;
    private String facilityCode;
}
