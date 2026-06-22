package com.maersk.wms.outbound.service.shipping;

import com.maersk.wms.outbound.domain.shipping.Carrier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of carrier change validation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrierChangeValidation {

    private boolean valid;
    private Carrier newCarrier;
    private String errorMessage;

    @Builder.Default
    private List<String> warnings = new ArrayList<>();

    // Impact analysis
    private boolean labelRegenerationRequired;
    private boolean rateChangeExpected;
    private boolean transitTimeImpacted;
}
