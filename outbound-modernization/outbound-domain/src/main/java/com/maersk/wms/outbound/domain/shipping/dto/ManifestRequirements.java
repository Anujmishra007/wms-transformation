package com.maersk.wms.outbound.domain.shipping.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO for carrier-specific manifest requirements.
 */
@Data
@Builder
public class ManifestRequirements {
    private boolean requiresTransmission;
    private boolean requiresScheduledPickup;
    private boolean requiresEndOfDay;
    private String transmissionFormat;
    private String transmissionUrl;
    private int cutoffHour;
    private int cutoffMinute;
    private boolean allowsLateClose;
}
