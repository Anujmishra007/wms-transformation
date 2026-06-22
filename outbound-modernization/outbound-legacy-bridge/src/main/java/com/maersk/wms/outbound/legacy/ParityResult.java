package com.maersk.wms.outbound.legacy;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Result from legacy stored procedure call for parity comparison.
 */
@Data
@Builder
public class ParityResult {

    private boolean success;
    private int errorCode;
    private String errorMessage;
    private String resultKey;
    private Map<String, Object> legacyResult;

    /**
     * Compare with modern service result for parity testing.
     */
    public ParityComparison compareTo(Object modernResult) {
        ParityComparison comparison = new ParityComparison();
        comparison.setLegacySuccess(this.success);
        comparison.setLegacyErrorCode(this.errorCode);
        comparison.setLegacyErrorMessage(this.errorMessage);
        comparison.setLegacyResult(this.legacyResult);

        if (modernResult instanceof ParityComparable comparable) {
            comparison.setModernSuccess(comparable.isSuccess());
            comparison.setModernErrorMessage(comparable.getErrorMessage());
            comparison.setModernResult(comparable.toMap());
            comparison.setParity(determinesParity(comparison));
        }

        return comparison;
    }

    private boolean determinesParity(ParityComparison comparison) {
        // Basic parity: both succeed or both fail
        if (comparison.isLegacySuccess() != comparison.isModernSuccess()) {
            return false;
        }
        // If both failed, check error codes match
        if (!comparison.isLegacySuccess()) {
            return comparison.getLegacyErrorCode() == comparison.getModernErrorCode();
        }
        // Both succeeded - parity achieved
        return true;
    }

    /**
     * Interface for modern results that can be compared for parity.
     */
    public interface ParityComparable {
        boolean isSuccess();
        String getErrorMessage();
        Map<String, Object> toMap();
    }

    /**
     * Detailed parity comparison result.
     */
    @Data
    public static class ParityComparison {
        private boolean parity;
        private boolean legacySuccess;
        private int legacyErrorCode;
        private String legacyErrorMessage;
        private Map<String, Object> legacyResult;

        private boolean modernSuccess;
        private int modernErrorCode;
        private String modernErrorMessage;
        private Map<String, Object> modernResult;

        private Map<String, FieldDiff> fieldDifferences;
    }

    /**
     * Field-level difference for parity analysis.
     */
    @Data
    @Builder
    public static class FieldDiff {
        private String fieldName;
        private Object legacyValue;
        private Object modernValue;
        private boolean matches;
    }
}
