package com.maersk.wms.inbound.workflow.receiving;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Signal to report damaged items during receiving.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DamageReportSignal {
    private String lineNumber;
    private String sku;
    private BigDecimal damagedQty;
    private String damageCode;
    private String damageDescription;
    private String disposition;  // SCRAP, RETURN_TO_VENDOR, REPACK, etc.
    private String userId;
}
