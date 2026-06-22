package com.maersk.wms.outbound.rules;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Result from shipping rule evaluation.
 */
@Data
public class ShippingRuleResult {

    private boolean shippingAllowed = true;
    private String recommendedCarrier;
    private String recommendedShipMethod;
    private BigDecimal estimatedFreight = BigDecimal.ZERO;
    private List<String> requiredDocuments = new ArrayList<>();
    private Map<String, String> labelAttributes;
    private List<String> validationErrors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();
    private boolean requiresCustoms = false;
    private boolean requiresHazmatDocs = false;

    public void addRequiredDocument(String document) {
        this.requiredDocuments.add(document);
    }

    public void addError(String error) {
        this.validationErrors.add(error);
        this.shippingAllowed = false;
    }

    public void addWarning(String warning) {
        this.warnings.add(warning);
    }

    public boolean hasErrors() {
        return !validationErrors.isEmpty();
    }
}
