package com.maersk.wms.inbound.rules;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of quality inspection determination rules.
 */
@Data
public class QualityInspectionResult {

    private boolean inspectionRequired;
    private String inspectionType;
    private int sampleSize;
    private int samplePercentage;
    private String inspectionLocation;
    private int priority;
    private List<String> inspectionChecks = new ArrayList<>();
    private List<String> reasons = new ArrayList<>();

    public void addInspectionCheck(String check) {
        inspectionChecks.add(check);
    }

    public void addReason(String reason) {
        reasons.add(reason);
    }
}
