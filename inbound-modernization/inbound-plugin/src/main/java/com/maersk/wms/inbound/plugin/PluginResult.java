package com.maersk.wms.inbound.plugin;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Result object returned by inbound plugins.
 */
@Data
@Builder
public class PluginResult {

    private boolean success;
    private boolean continueProcessing;
    private String message;
    private String errorCode;

    @Builder.Default
    private List<String> warnings = new ArrayList<>();

    @Builder.Default
    private Map<String, Object> data = new HashMap<>();

    public static PluginResult success() {
        return PluginResult.builder()
                .success(true)
                .continueProcessing(true)
                .build();
    }

    public static PluginResult success(String message) {
        return PluginResult.builder()
                .success(true)
                .continueProcessing(true)
                .message(message)
                .build();
    }

    public static PluginResult failure(String errorCode, String message) {
        return PluginResult.builder()
                .success(false)
                .continueProcessing(false)
                .errorCode(errorCode)
                .message(message)
                .build();
    }

    public static PluginResult skip() {
        return PluginResult.builder()
                .success(true)
                .continueProcessing(true)
                .message("Plugin skipped")
                .build();
    }

    public PluginResult withData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public PluginResult withWarning(String warning) {
        this.warnings.add(warning);
        return this;
    }
}
