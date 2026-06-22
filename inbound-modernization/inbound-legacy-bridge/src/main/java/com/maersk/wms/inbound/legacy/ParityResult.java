package com.maersk.wms.inbound.legacy;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of parity comparison between modernized code and legacy SP.
 */
@Data
public class ParityResult {

    private boolean match;
    private boolean bothFailed;
    private long modernDurationMs;
    private long legacyDurationMs;
    private String modernError;
    private String legacyError;
    private List<FieldDifference> differences = new ArrayList<>();

    public void addDifference(String fieldName, Object modernValue, Object legacyValue) {
        differences.add(new FieldDifference(fieldName, modernValue, legacyValue));
    }

    public boolean hasDifferences() {
        return !differences.isEmpty();
    }

    @Data
    public static class FieldDifference {
        private final String fieldName;
        private final Object modernValue;
        private final Object legacyValue;

        public FieldDifference(String fieldName, Object modernValue, Object legacyValue) {
            this.fieldName = fieldName;
            this.modernValue = modernValue;
            this.legacyValue = legacyValue;
        }
    }
}
