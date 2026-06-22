package com.maersk.wms.outbound.rules;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * Rules engine for outbound operations.
 * Uses Drools for allocation, wave planning, and shipping rules.
 */
@Component
public class OutboundRulesEngine {

    private KieContainer kieContainer;

    @PostConstruct
    public void init() {
        KieServices kieServices = KieServices.Factory.get();
        this.kieContainer = kieServices.getKieClasspathContainer();
    }

    /**
     * Evaluate allocation rules.
     */
    public AllocationRuleResult evaluateAllocationRules(AllocationRuleFacts facts) {
        KieSession session = kieContainer.newKieSession("outbound-allocation-rules");
        try {
            AllocationRuleResult result = new AllocationRuleResult();
            session.insert(facts);
            session.insert(result);
            session.fireAllRules();
            return result;
        } finally {
            session.dispose();
        }
    }

    /**
     * Evaluate wave planning rules.
     */
    public WaveRuleResult evaluateWaveRules(WaveRuleFacts facts) {
        KieSession session = kieContainer.newKieSession("outbound-wave-rules");
        try {
            WaveRuleResult result = new WaveRuleResult();
            session.insert(facts);
            session.insert(result);
            session.fireAllRules();
            return result;
        } finally {
            session.dispose();
        }
    }

    /**
     * Evaluate shipping rules.
     */
    public ShippingRuleResult evaluateShippingRules(ShippingRuleFacts facts) {
        KieSession session = kieContainer.newKieSession("outbound-shipping-rules");
        try {
            ShippingRuleResult result = new ShippingRuleResult();
            session.insert(facts);
            session.insert(result);
            session.fireAllRules();
            return result;
        } finally {
            session.dispose();
        }
    }

    /**
     * Evaluate cartonization rules.
     */
    public CartonizationRuleResult evaluateCartonizationRules(CartonizationRuleFacts facts) {
        KieSession session = kieContainer.newKieSession("outbound-cartonization-rules");
        try {
            CartonizationRuleResult result = new CartonizationRuleResult();
            session.insert(facts);
            session.insert(result);
            session.fireAllRules();
            return result;
        } finally {
            session.dispose();
        }
    }
}
