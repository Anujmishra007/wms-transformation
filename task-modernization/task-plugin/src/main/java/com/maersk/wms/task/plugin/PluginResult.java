package com.maersk.wms.task.plugin;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Result object returned from plugin executions.
 */
@Data
@Builder
public class PluginResult {

    private boolean success;
    private boolean shouldContinue;
    private String message;
    private String errorCode;

    @Builder.Default
    private List<String> warnings = new ArrayList<>();

    @Builder.Default
    private Map<String, Object> data = new HashMap<>();

    public static PluginResult success() {
        return PluginResult.builder()
                .success(true)
                .shouldContinue(true)
                .build();
    }

    public static PluginResult success(String message) {
        return PluginResult.builder()
                .success(true)
                .shouldContinue(true)
                .message(message)
                .build();
    }

    public static PluginResult failure(String errorCode, String message) {
        return PluginResult.builder()
                .success(false)
                .shouldContinue(false)
                .errorCode(errorCode)
                .message(message)
                .build();
    }

    public static PluginResult skip() {
        return PluginResult.builder()
                .success(true)
                .shouldContinue(true)
                .message("Plugin skipped")
                .build();
    }

    public static PluginResult stopProcessing(String message) {
        return PluginResult.builder()
                .success(true)
                .shouldContinue(false)
                .message(message)
                .build();
    }

    public void addWarning(String warning) {
        warnings.add(warning);
    }

    public void setData(String key, Object value) {
        data.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(String key) {
        return (T) data.get(key);
    }

    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
}
