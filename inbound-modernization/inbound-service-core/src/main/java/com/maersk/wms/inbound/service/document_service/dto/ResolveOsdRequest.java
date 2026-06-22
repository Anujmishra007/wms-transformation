package com.maersk.wms.inbound.service.document_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ResolveOsdRequest {
    private String resolution;
    private String resolvedBy;
    private BigDecimal creditAmount;
    private BigDecimal debitAmount;
}
