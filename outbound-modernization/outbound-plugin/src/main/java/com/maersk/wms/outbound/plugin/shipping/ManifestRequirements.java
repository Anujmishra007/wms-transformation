package com.maersk.wms.outbound.plugin.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Carrier-specific manifest requirements.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManifestRequirements {

    private boolean requiresTransmission;
    private boolean requiresPickupSchedule;
    private String transmissionMethod;  // API, EDI, EMAIL
    private String manifestCutoffTime;
    private int maxPackagesPerManifest;
    private boolean allowsMultipleManifestsPerDay;
    private String[] requiredFields;
}
