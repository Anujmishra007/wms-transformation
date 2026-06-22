package com.maersk.wms.task.rules.facts;

import com.maersk.wms.task.domain.enums.TaskPriority;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Result object from task priority rules evaluation.
 */
@Data
public class TaskPriorityResult {

    private TaskPriority recommendedPriority;
    private Integer priorityScore;
    private Boolean shouldEscalate;
    private Integer escalationMinutes;
    private List<String> appliedRules = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();

    public void addAppliedRule(String ruleName) {
        appliedRules.add(ruleName);
    }

    public void addWarning(String warning) {
        warnings.add(warning);
    }

    public void adjustPriorityScore(int adjustment) {
        if (priorityScore == null) {
            priorityScore = 0;
        }
        priorityScore += adjustment;
    }
}
