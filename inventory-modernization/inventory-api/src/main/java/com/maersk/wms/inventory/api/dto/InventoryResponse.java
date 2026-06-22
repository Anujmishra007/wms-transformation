package com.maersk.wms.inventory.api.dto;

import com.maersk.wms.inventory.domain.LotxLocxId;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class InventoryResponse {
    private String lotxLocxIdKey;
    private String sku;
    private String lot;
    private String location;
    private String lpn;
    private BigDecimal qty;
    private BigDecimal qtyAvailable;
    private BigDecimal qtyAllocated;
    private String status;
    private String holdCode;

    public static InventoryResponse from(LotxLocxId inv) {
        return InventoryResponse.builder()
                .lotxLocxIdKey(inv.getLotxLocxIdKey())
                .sku(inv.getSku())
                .lot(inv.getLot())
                .location(inv.getLocation())
                .lpn(inv.getId())
                .qty(inv.getQty())
                .qtyAvailable(inv.getQtyAvailable())
                .qtyAllocated(inv.getQtyAllocated())
                .status(inv.getStatus().name())
                .holdCode(inv.getHoldCode())
                .build();
    }
}
