package com.maersk.wms.task.rules.facts;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Result object from task assignment rules evaluation.
 */
@Data
public class TaskAssignmentResult {

    private Boolean isEligible;
    private Integer assignmentScore;
    private String recommendedUser;
    private String ineligibilityReason;
    private List<String> appliedRules = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();

    public void addAppliedRule(String ruleName) {
        appliedRules.add(ruleName);
    }

    public void addWarning(String warning) {
        warnings.add(warning);
    }

    public void adjustScore(int adjustment) {
        if (assignmentScore == null) {
            assignmentScore = 0;
        }
        assignmentScore += adjustment;
    }

    public void markIneligible(String reason) {
        this.isEligible = false;
        this.ineligibilityReason = reason;
    }
}
