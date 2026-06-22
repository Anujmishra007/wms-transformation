package com.maersk.wms.inventory.rules;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpiryCheckResult {
    private boolean isValid;
    private boolean isExpired;
    private boolean isExpiringSoon;
    private int daysUntilExpiry;
    private String action;
}
