package com.maersk.wms.masterdata.plugin;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Result from plugin execution.
 */
@Data
@Builder
public class PluginResult {

    private boolean success;
    private String errorCode;
    private String errorMessage;
    private boolean continueProcessing;

    @Builder.Default
    private Map<String, Object> data = new HashMap<>();

    public static PluginResult success() {
        return PluginResult.builder()
                .success(true)
                .continueProcessing(true)
                .build();
    }

    public static PluginResult success(Map<String, Object> data) {
        return PluginResult.builder()
                .success(true)
                .continueProcessing(true)
                .data(data)
                .build();
    }

    public static PluginResult failure(String errorMessage) {
        return PluginResult.builder()
                .success(false)
                .errorMessage(errorMessage)
                .continueProcessing(false)
                .build();
    }

    public static PluginResult failure(String errorCode, String errorMessage) {
        return PluginResult.builder()
                .success(false)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .continueProcessing(false)
                .build();
    }

    public static PluginResult skip() {
        return PluginResult.builder()
                .success(true)
                .continueProcessing(true)
                .build();
    }

    public void addData(String key, Object value) {
        this.data.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(String key) {
        return (T) this.data.get(key);
    }
}
