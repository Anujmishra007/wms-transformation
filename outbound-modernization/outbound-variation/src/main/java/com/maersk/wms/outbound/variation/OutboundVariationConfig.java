package com.maersk.wms.outbound.variation;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Configuration for client-specific outbound variations.
 */
@Data
@Builder
public class OutboundVariationConfig {

    private String clientCode;
    private String facilityCode;

    // Allocation settings
    private String allocationStrategy;  // FIFO, FEFO, LIFO, CUSTOM
    private boolean autoAllocationEnabled;
    private boolean partialAllocationAllowed;
    private int allocationRetryCount;
    private boolean expirationCheckEnabled;
    private int minimumDaysToExpiration;

    // Wave settings
    private String waveGroupingStrategy;  // CARRIER, CUSTOMER, ZONE, CUSTOM
    private int maxOrdersPerWave;
    private int maxLinesPerWave;
    private int maxUnitsPerWave;
    private boolean autoReleaseEnabled;

    // Packing settings
    private String cartonizationStrategy;  // SINGLE_SKU, MULTI_SKU, WEIGHT_BASED
    private boolean giftWrapEnabled;
    private String defaultCartonType;
    private boolean packSlipRequired;
    private String packSlipFormat;

    // Shipping settings
    private String defaultCarrier;
    private boolean rateShopEnabled;
    private boolean manifestRequired;
    private boolean trackingRequired;
    private String labelFormat;
    private boolean commercialInvoiceRequired;

    // Notification settings
    private boolean shipNotificationEnabled;
    private boolean asnEnabled;
    private String notificationFormat;

    // Custom parameters
    private Map<String, String> customParameters;

    /**
     * Create default configuration.
     */
    public static OutboundVariationConfig defaultConfig() {
        return OutboundVariationConfig.builder()
                .allocationStrategy("FIFO")
                .autoAllocationEnabled(false)
                .partialAllocationAllowed(true)
                .allocationRetryCount(3)
                .expirationCheckEnabled(true)
                .minimumDaysToExpiration(30)
                .waveGroupingStrategy("CARRIER")
                .maxOrdersPerWave(100)
                .maxLinesPerWave(1000)
                .maxUnitsPerWave(10000)
                .autoReleaseEnabled(false)
                .cartonizationStrategy("MULTI_SKU")
                .giftWrapEnabled(false)
                .defaultCartonType("STANDARD")
                .packSlipRequired(true)
                .packSlipFormat("STANDARD")
                .rateShopEnabled(false)
                .manifestRequired(true)
                .trackingRequired(true)
                .labelFormat("ZPL")
                .commercialInvoiceRequired(false)
                .shipNotificationEnabled(true)
                .asnEnabled(false)
                .notificationFormat("EDI")
                .build();
    }
}
