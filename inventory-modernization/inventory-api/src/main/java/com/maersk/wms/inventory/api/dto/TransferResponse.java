package com.maersk.wms.inventory.api.dto;

import com.maersk.wms.inventory.domain.InventoryTransfer;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class TransferResponse {
    private String transferKey;
    private String status;
    private BigDecimal transferredQty;

    public static TransferResponse from(InventoryTransfer transfer) {
        return TransferResponse.builder()
                .transferKey(transfer.getTransferKey())
                .status(transfer.getStatus().name())
                .transferredQty(transfer.getTransferQty())
                .build();
    }
}
