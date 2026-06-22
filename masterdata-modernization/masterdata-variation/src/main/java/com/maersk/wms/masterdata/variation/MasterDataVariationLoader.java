package com.maersk.wms.masterdata.variation;

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
public class MasterDataVariationLoader {

    private final ResourceLoader resourceLoader;
    private final String configBasePath;
    private final Yaml yaml = new Yaml();

    public MasterDataVariationLoader(
            ResourceLoader resourceLoader,
            @Value("${masterdata.variation.config-path:classpath:config/clients}") String configBasePath) {
        this.resourceLoader = resourceLoader;
        this.configBasePath = configBasePath;
    }

    public Optional<MasterDataVariationConfig> loadConfig(String clientCode, String facilityCode) {
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
    private MasterDataVariationConfig mapToConfig(Map<String, Object> configMap, String clientCode, String facilityCode) {
        Map<String, Object> item = (Map<String, Object>) configMap.getOrDefault("item", Map.of());
        Map<String, Object> location = (Map<String, Object>) configMap.getOrDefault("location", Map.of());
        Map<String, Object> customer = (Map<String, Object>) configMap.getOrDefault("customer", Map.of());
        Map<String, Object> carrier = (Map<String, Object>) configMap.getOrDefault("carrier", Map.of());

        return MasterDataVariationConfig.builder()
                .clientCode(clientCode)
                .facilityCode(facilityCode)
                // Item
                .autoSkuGenerationEnabled((Boolean) item.getOrDefault("autoSkuGeneration", false))
                .skuFormat((String) item.getOrDefault("skuFormat", "{PREFIX}-{SEQUENCE}"))
                .defaultItemGroup((String) item.getOrDefault("defaultGroup", null))
                .defaultItemClass((String) item.getOrDefault("defaultClass", null))
                .requirePhysicalDimensions((Boolean) item.getOrDefault("requireDimensions", false))
                .requireWeight((Boolean) item.getOrDefault("requireWeight", true))
                // Location
                .locationCodeFormat((String) location.getOrDefault("codeFormat", "{ZONE}-{AISLE}-{BAY}-{LEVEL}-{POSITION}"))
                .autoLocationCodeGeneration((Boolean) location.getOrDefault("autoCodeGeneration", true))
                .defaultLocationType((String) location.getOrDefault("defaultType", "RESERVE"))
                .defaultStorageType((String) location.getOrDefault("defaultStorageType", "STANDARD"))
                .requireLocationDimensions((Boolean) location.getOrDefault("requireDimensions", false))
                // Customer
                .addressValidationRequired((Boolean) customer.getOrDefault("addressValidation", false))
                .defaultCustomerType((String) customer.getOrDefault("defaultType", "SHIPTO"))
                .defaultServiceLevel((String) customer.getOrDefault("defaultServiceLevel", "STANDARD"))
                // Carrier
                .carrierApiRequired((Boolean) carrier.getOrDefault("apiRequired", false))
                .defaultLabelFormat((String) carrier.getOrDefault("labelFormat", "ZPL"))
                // Custom
                .customParameters((Map<String, String>) configMap.getOrDefault("custom", Map.of()))
                .build();
    }
}
