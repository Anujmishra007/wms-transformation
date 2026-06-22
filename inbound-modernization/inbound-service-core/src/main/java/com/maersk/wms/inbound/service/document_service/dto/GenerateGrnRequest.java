package com.maersk.wms.inbound.service.document_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class GenerateGrnRequest {
    private String storerKey;
    private String receiptKey;
    private String poKey;
    private String asnKey;
    private String vendorKey;
    private String vendorInvoice;
    private String deliveryNote;
    private String generatedBy;
    private String notes;
    private List<GenerateGrnDetailRequest> details;
}
