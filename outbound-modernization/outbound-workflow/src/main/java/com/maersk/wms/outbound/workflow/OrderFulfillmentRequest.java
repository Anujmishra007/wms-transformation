package com.maersk.wms.outbound.workflow;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Request to start an order fulfillment workflow.
 */
@Data
@Builder
public class OrderFulfillmentRequest {

    private String orderNumber;
    private String clientCode;
    private String facilityCode;
    private String userId;

    // Processing options
    private boolean autoAllocate;
    private boolean autoRelease;
    private boolean autoPack;
    private boolean autoShip;

    // Wave context (if part of wave)
    private String waveNumber;

    // Additional context
    private Map<String, String> metadata;
}
