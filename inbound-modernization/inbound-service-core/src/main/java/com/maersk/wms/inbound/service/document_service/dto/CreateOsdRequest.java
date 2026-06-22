package com.maersk.wms.inbound.service.document_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateOsdRequest {
    private String storerKey;
    private String receiptKey;
    private String receiptDetailKey;
    private String poKey;
    private String asnKey;
    private String vendorKey;
    private String carrierCode;
    private String reportedBy;
    private String notes;
    private List<CreateOsdDetailRequest> details;
}
