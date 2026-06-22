package com.maersk.wms.inventory.rules;

import com.maersk.wms.inventory.domain.LotxLocxId;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpiryFacts {
    private LotxLocxId inventory;
    private ExpiryCheckResult result;
}
