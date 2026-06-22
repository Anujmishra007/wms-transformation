package com.maersk.wms.inbound.service.document_service.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreateAsnRequest {
    private String storerKey;
    private String externalAsnNumber;
    private String asnType;
    private String poKey;
    private String vendorKey;
    private String carrierCode;
    private String carrierName;
    private String trailerNumber;
    private String sealNumber;
    private String billOfLading;
    private String proNumber;
    private LocalDate expectedDate;
    private LocalDate shipDate;
    private String notes;
    private List<CreateAsnDetailRequest> details;
}
