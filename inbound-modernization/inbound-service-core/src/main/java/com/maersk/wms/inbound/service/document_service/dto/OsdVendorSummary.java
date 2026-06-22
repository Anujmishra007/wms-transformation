package com.maersk.wms.inbound.service.document_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OsdVendorSummary {
    private String vendorKey;
    private int totalOsds;
    private BigDecimal totalOverageQty;
    private BigDecimal totalShortageQty;
    private BigDecimal totalDamageQty;
}
