package com.maersk.wms.outbound.variation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

/**
 * Loads client variation configurations from YAML files.
 */
@Component
@Slf4j
public class OutboundVariationLoader {

    private final ResourceLoader resourceLoader;
    private final String configBasePath;
    private final Yaml yaml = new Yaml();

    public OutboundVariationLoader(
            ResourceLoader resourceLoader,
            @Value("${outbound.variation.config-path:classpath:config/clients}") String configBasePath) {
        this.resourceLoader = resourceLoader;
        this.configBasePath = configBasePath;
    }

    /**
     * Load configuration for client and optional facility.
     */
    public Optional<OutboundVariationConfig> loadConfig(String clientCode, String facilityCode) {
        String configPath = buildConfigPath(clientCode, facilityCode);

        try {
            Resource resource = resourceLoader.getResource(configPath);
            if (resource.exists()) {
                try (InputStream is = resource.getInputStream()) {
                    Map<String, Object> configMap = yaml.load(is);
                    return Optional.of(mapToConfig(configMap, clientCode, facilityCode));
                }
            }
        } catch (Exception e) {
            log.warn("Failed to load variation config from: {}", configPath, e);
        }

        return Optional.empty();
    }

    private String buildConfigPath(String clientCode, String facilityCode) {
        if (facilityCode != null && !facilityCode.isEmpty()) {
            return configBasePath + "/" + clientCode.toLowerCase() + "_" + facilityCode.toLowerCase() + ".yaml";
        }
        return configBasePath + "/" + clientCode.toLowerCase() + ".yaml";
    }

    @SuppressWarnings("unchecked")
    private OutboundVariationConfig mapToConfig(Map<String, Object> configMap, String clientCode, String facilityCode) {
        Map<String, Object> allocation = (Map<String, Object>) configMap.getOrDefault("allocation", Map.of());
        Map<String, Object> wave = (Map<String, Object>) configMap.getOrDefault("wave", Map.of());
        Map<String, Object> packing = (Map<String, Object>) configMap.getOrDefault("packing", Map.of());
        Map<String, Object> shipping = (Map<String, Object>) configMap.getOrDefault("shipping", Map.of());
        Map<String, Object> notification = (Map<String, Object>) configMap.getOrDefault("notification", Map.of());

        return OutboundVariationConfig.builder()
                .clientCode(clientCode)
                .facilityCode(facilityCode)
                // Allocation
                .allocationStrategy((String) allocation.getOrDefault("strategy", "FIFO"))
                .autoAllocationEnabled((Boolean) allocation.getOrDefault("autoEnabled", false))
                .partialAllocationAllowed((Boolean) allocation.getOrDefault("partialAllowed", true))
                .allocationRetryCount((Integer) allocation.getOrDefault("retryCount", 3))
                .expirationCheckEnabled((Boolean) allocation.getOrDefault("expirationCheck", true))
                .minimumDaysToExpiration((Integer) allocation.getOrDefault("minDaysToExpiration", 30))
                // Wave
                .waveGroupingStrategy((String) wave.getOrDefault("groupingStrategy", "CARRIER"))
                .maxOrdersPerWave((Integer) wave.getOrDefault("maxOrders", 100))
                .maxLinesPerWave((Integer) wave.getOrDefault("maxLines", 1000))
                .maxUnitsPerWave((Integer) wave.getOrDefault("maxUnits", 10000))
                .autoReleaseEnabled((Boolean) wave.getOrDefault("autoRelease", false))
                // Packing
                .cartonizationStrategy((String) packing.getOrDefault("cartonization", "MULTI_SKU"))
                .giftWrapEnabled((Boolean) packing.getOrDefault("giftWrap", false))
                .defaultCartonType((String) packing.getOrDefault("defaultCarton", "STANDARD"))
                .packSlipRequired((Boolean) packing.getOrDefault("packSlipRequired", true))
                .packSlipFormat((String) packing.getOrDefault("packSlipFormat", "STANDARD"))
                // Shipping
                .defaultCarrier((String) shipping.getOrDefault("defaultCarrier", null))
                .rateShopEnabled((Boolean) shipping.getOrDefault("rateShop", false))
                .manifestRequired((Boolean) shipping.getOrDefault("manifestRequired", true))
                .trackingRequired((Boolean) shipping.getOrDefault("trackingRequired", true))
                .labelFormat((String) shipping.getOrDefault("labelFormat", "ZPL"))
                .commercialInvoiceRequired((Boolean) shipping.getOrDefault("commercialInvoice", false))
                // Notification
                .shipNotificationEnabled((Boolean) notification.getOrDefault("shipNotification", true))
                .asnEnabled((Boolean) notification.getOrDefault("asn", false))
                .notificationFormat((String) notification.getOrDefault("format", "EDI"))
                // Custom
                .customParameters((Map<String, String>) configMap.getOrDefault("custom", Map.of()))
                .build();
    }
}
