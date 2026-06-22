package com.maersk.wms.inbound.service.document_service.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdatePoRequest {
    private LocalDate expectedDate;
    private String buyerName;
    private String notes;
}
