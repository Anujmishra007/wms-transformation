package com.maersk.wms.inbound.shared.kernel.exceptions;

import com.maersk.wms.inbound.shared.kernel.events.InboundBoundedContext;

/**
 * Exception thrown when a business rule is violated.
 *
 * Part of Shared Kernel - used for business rule enforcement across bounded contexts.
 */
public class BusinessRuleException extends InboundException {

    private final String ruleCode;
    private final String ruleName;

    public BusinessRuleException(String message, InboundBoundedContext sourceContext) {
        super(message, sourceContext, "BUSINESS_RULE_VIOLATION");
        this.ruleCode = null;
        this.ruleName = null;
    }

    public BusinessRuleException(String message, String ruleCode, InboundBoundedContext sourceContext) {
        super(message, sourceContext, "BUSINESS_RULE_VIOLATION");
        this.ruleCode = ruleCode;
        this.ruleName = null;
    }

    public BusinessRuleException(String message, String ruleCode, String ruleName, InboundBoundedContext sourceContext) {
        super(message, sourceContext, "BUSINESS_RULE_VIOLATION");
        this.ruleCode = ruleCode;
        this.ruleName = ruleName;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public String getRuleName() {
        return ruleName;
    }
}
