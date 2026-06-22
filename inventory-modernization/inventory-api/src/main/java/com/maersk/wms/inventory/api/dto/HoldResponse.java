package com.maersk.wms.inventory.api.dto;

import com.maersk.wms.inventory.domain.InventoryHold;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HoldResponse {
    private String holdKey;
    private String holdCode;
    private String scope;
    private boolean active;

    public static HoldResponse from(InventoryHold hold) {
        return HoldResponse.builder()
                .holdKey(hold.getHoldKey())
                .holdCode(hold.getHoldCode())
                .scope(hold.getScope().name())
                .active(hold.isActive())
                .build();
    }
}
