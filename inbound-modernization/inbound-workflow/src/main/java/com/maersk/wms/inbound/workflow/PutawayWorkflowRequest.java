package com.maersk.wms.inbound.workflow;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Request object for putaway workflow.
 */
@Data
@Builder
public class PutawayWorkflowRequest {

    private String clientCode;
    private String countryCode;
    private String warehouseCode;
    private String userId;

    private String receiptKey;
    private List<String> taskKeys;
    private String assignedUser;
    private boolean autoAssign;
}
