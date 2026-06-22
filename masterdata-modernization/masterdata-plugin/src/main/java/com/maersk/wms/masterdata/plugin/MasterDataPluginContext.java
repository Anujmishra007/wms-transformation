package com.maersk.wms.masterdata.plugin;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Context information for master data plugin execution.
 */
@Data
@Builder
public class MasterDataPluginContext {

    private String clientCode;
    private String facilityCode;
    private String userId;
    private String operationType;

    @Builder.Default
    private Map<String, String> parameters = new HashMap<>();

    public void addParameter(String key, String value) {
        this.parameters.put(key, value);
    }

    public String getParameter(String key) {
        return this.parameters.get(key);
    }

    public String getParameter(String key, String defaultValue) {
        return this.parameters.getOrDefault(key, defaultValue);
    }
}
