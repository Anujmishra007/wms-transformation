package com.maersk.wms.inbound.service.document_service.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreatePoRequest {
    private String storerKey;
    private String externalPoNumber;
    private String poType;
    private String vendorKey;
    private String vendorName;
    private LocalDate expectedDate;
    private String buyerName;
    private String buyerReference;
    private String notes;
    private List<CreatePoDetailRequest> details;
}
