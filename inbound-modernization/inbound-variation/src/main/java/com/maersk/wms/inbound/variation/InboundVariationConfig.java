package com.maersk.wms.inbound.variation;

import lombok.Builder;
import lombok.Data;

/**
 * Configuration for client-specific inbound variations.
 */
@Data
@Builder
public class InboundVariationConfig {

    private String clientCode;
    private String warehouseCode;

    // Receiving settings
    private boolean overReceiveAllowed;
    private int overReceiveTolerancePercent;
    private boolean blindReceiveAllowed;
    private boolean asnRequired;
    private boolean poRequired;

    // Putaway settings
    private String defaultPutawayStrategy;
    private boolean autoPutawayEnabled;
    private boolean consolidationAllowed;
    private boolean mixedLotAllowed;

    // Quality settings
    private boolean qualityInspectionEnabled;
    private int defaultSamplePercent;

    // Shelf life settings
    private int minShelfLifePercent;
    private boolean expirationDateRequired;

    // LPN settings
    private boolean lpnRequired;
    private boolean autoGenerateLpn;
    private String lpnPrefix;

    // Lot settings
    private boolean lotRequired;
    private boolean autoGenerateLot;

    /**
     * Create default configuration.
     */
    public static InboundVariationConfig defaultConfig() {
        return InboundVariationConfig.builder()
                .overReceiveAllowed(false)
                .overReceiveTolerancePercent(0)
                .blindReceiveAllowed(false)
                .asnRequired(false)
                .poRequired(false)
                .defaultPutawayStrategy("DIRECTED")
                .autoPutawayEnabled(true)
                .consolidationAllowed(true)
                .mixedLotAllowed(false)
                .qualityInspectionEnabled(false)
                .defaultSamplePercent(0)
                .minShelfLifePercent(50)
                .expirationDateRequired(false)
                .lpnRequired(true)
                .autoGenerateLpn(true)
                .lpnPrefix("LP")
                .lotRequired(false)
                .autoGenerateLot(false)
                .build();
    }
}
