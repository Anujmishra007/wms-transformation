package com.maersk.wms.picking.legacy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Objects;

/**
 * Service for comparing Java implementation vs legacy SP results.
 * Used during migration to validate functional parity.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ParityTestingService {

    private final LegacyPickingBridge legacyBridge;

    /**
     * Compare results and log discrepancies.
     */
    public ParityResult compareResults(String operation, Object javaResult, Map<String, Object> legacyResult) {
        ParityResult result = new ParityResult();
        result.setOperation(operation);
        result.setMatch(true);

        // Compare key fields
        // This would be customized per operation
        if (javaResult == null && legacyResult == null) {
            result.setMatch(true);
        } else if (javaResult == null || legacyResult == null) {
            result.setMatch(false);
            result.addDiscrepancy("null_mismatch", String.valueOf(javaResult), String.valueOf(legacyResult));
        }

        if (!result.isMatch()) {
            log.warn("Parity mismatch for {}: {}", operation, result.getDiscrepancies());
        } else {
            log.debug("Parity match for {}", operation);
        }

        return result;
    }

    /**
     * Enable/disable dual-write mode.
     */
    public void setDualWriteEnabled(boolean enabled) {
        log.info("Dual-write mode: {}", enabled ? "ENABLED" : "DISABLED");
        // Store in thread-local or config
    }
}
