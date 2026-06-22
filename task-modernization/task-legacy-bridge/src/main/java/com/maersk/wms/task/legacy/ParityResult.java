package com.maersk.wms.task.legacy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Result of SP parity comparison.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParityResult {

    private boolean matched;
    private String operation;
    private String entityId;

    private Map<String, Object> modernResult;
    private Map<String, Object> legacyResult;

    private List<String> differences;
    private String errorMessage;

    public boolean hasDifferences() {
        return differences != null && !differences.isEmpty();
    }
}
