package com.maersk.wms.inbound.service.operations_service.dto;

import com.maersk.wms.inbound.domain.operations_service.CrossdockType;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateCrossdockRequest {
    private String storerKey;
    private String receiptKey;
    private CrossdockType crossdockType;
    private String sku;
    private String inboundLpn;
    private BigDecimal quantity;
    private String uom;
    private String orderKey;
    private String waveKey;
    private int priority;
    private boolean opportunistic;
    private boolean planned;
}
