package com.maersk.wms.task.shared.kernel.exceptions;

/**
 * Exception thrown when a priority rule is not found.
 */
public class PriorityRuleNotFoundException extends TaskManagementException {

    public PriorityRuleNotFoundException(String ruleKey) {
        super("PRIORITY_RULE_NOT_FOUND", "Priority rule not found: " + ruleKey);
    }
}
