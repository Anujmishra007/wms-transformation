package com.maersk.wms.common.context;

import lombok.Builder;
import lombok.Data;

/**
 * Tenant information extracted from JWT.
 */
@Data
@Builder
public class TenantInfo {
    private String countryCode;
    private String clientCode;
    private String warehouseCode;
    private String userId;
    private String languageId;
    private String appId;
}
