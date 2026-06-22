package com.maersk.wms.inbound.service.document_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AsnArrivalRequest {
    private String dockDoor;
    private LocalDateTime arrivalTime;
    private String trailerNumber;
    private String sealNumber;
}
