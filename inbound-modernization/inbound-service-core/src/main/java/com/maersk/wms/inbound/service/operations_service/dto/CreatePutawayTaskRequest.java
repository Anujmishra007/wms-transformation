package com.maersk.wms.inbound.service.operations_service.dto;

import com.maersk.wms.inbound.domain.operations_service.PutawayTaskType;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreatePutawayTaskRequest {
    private String storerKey;
    private String receiptKey;
    private String sourceLpn;
    private String sku;
    private BigDecimal quantity;
    private String uom;
    private String fromLocation;
    private String suggestedLocation;
    private PutawayTaskType taskType;
    private String putawayStrategy;
    private String putawayZone;
    private int priority;
    private boolean crossdock;
}
