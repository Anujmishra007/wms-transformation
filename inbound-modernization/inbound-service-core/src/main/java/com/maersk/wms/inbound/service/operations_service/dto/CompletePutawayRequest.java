package com.maersk.wms.inbound.service.operations_service.dto;

import lombok.Data;

@Data
public class CompletePutawayRequest {
    private String actualLocation;
    private String targetLpn;
    private String completedBy;
}
