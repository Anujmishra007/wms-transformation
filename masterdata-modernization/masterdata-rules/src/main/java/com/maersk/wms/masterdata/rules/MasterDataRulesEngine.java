package com.maersk.wms.masterdata.rules;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * Rules engine for master data operations.
 * Uses Drools for validation and transformation rules.
 */
@Component
public class MasterDataRulesEngine {

    private KieContainer kieContainer;

    @PostConstruct
    public void init() {
        KieServices kieServices = KieServices.Factory.get();
        this.kieContainer = kieServices.getKieClasspathContainer();
    }

    /**
     * Evaluate item validation rules.
     */
    public ItemValidationResult evaluateItemRules(ItemValidationFacts facts) {
        KieSession session = kieContainer.newKieSession("masterdata-item-rules");
        try {
            ItemValidationResult result = new ItemValidationResult();
            session.insert(facts);
            session.insert(result);
            session.fireAllRules();
            return result;
        } finally {
            session.dispose();
        }
    }

    /**
     * Evaluate location validation rules.
     */
    public LocationValidationResult evaluateLocationRules(LocationValidationFacts facts) {
        KieSession session = kieContainer.newKieSession("masterdata-location-rules");
        try {
            LocationValidationResult result = new LocationValidationResult();
            session.insert(facts);
            session.insert(result);
            session.fireAllRules();
            return result;
        } finally {
            session.dispose();
        }
    }

    /**
     * Evaluate customer validation rules.
     */
    public CustomerValidationResult evaluateCustomerRules(CustomerValidationFacts facts) {
        KieSession session = kieContainer.newKieSession("masterdata-customer-rules");
        try {
            CustomerValidationResult result = new CustomerValidationResult();
            session.insert(facts);
            session.insert(result);
            session.fireAllRules();
            return result;
        } finally {
            session.dispose();
        }
    }

    /**
     * Evaluate carrier validation rules.
     */
    public CarrierValidationResult evaluateCarrierRules(CarrierValidationFacts facts) {
        KieSession session = kieContainer.newKieSession("masterdata-carrier-rules");
        try {
            CarrierValidationResult result = new CarrierValidationResult();
            session.insert(facts);
            session.insert(result);
            session.fireAllRules();
            return result;
        } finally {
            session.dispose();
        }
    }
}
