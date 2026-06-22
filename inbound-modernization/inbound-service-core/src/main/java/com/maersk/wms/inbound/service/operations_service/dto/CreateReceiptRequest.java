package com.maersk.wms.inbound.service.operations_service.dto;

import com.maersk.wms.inbound.domain.operations_service.ReceiptType;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateReceiptRequest {
    private String storerKey;
    private ReceiptType receiptType;
    private String poKey;
    private String asnKey;
    private String vendorKey;
    private String carrierCode;
    private String trailerNumber;
    private String dockDoor;
    private LocalDate expectedDate;
    private String notes;
    private String createdBy;
}
