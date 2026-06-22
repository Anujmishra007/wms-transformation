package com.maersk.wms.inventory.rules;

import com.maersk.wms.inventory.domain.LotxLocxId;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AllocationFacts {
    private List<LotxLocxId> inventory;
    private List<LotxLocxId> sortedInventory;
    private String fifoVariant;
}
