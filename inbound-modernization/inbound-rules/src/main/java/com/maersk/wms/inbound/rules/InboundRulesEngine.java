package com.maersk.wms.inbound.rules;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Collection;

/**
 * Drools rules engine for inbound operations.
 * Manages business rules for receiving, putaway location selection, and quality inspection.
 */
@Component
public class InboundRulesEngine {

    private KieContainer kieContainer;

    @PostConstruct
    public void init() {
        KieServices kieServices = KieServices.Factory.get();
        this.kieContainer = kieServices.getKieClasspathContainer();
    }

    /**
     * Execute putaway location rules.
     */
    public PutawayLocationResult determinePutawayLocation(PutawayLocationFacts facts) {
        KieSession session = kieContainer.newKieSession("inbound-rules-session");
        try {
            PutawayLocationResult result = new PutawayLocationResult();
            session.insert(facts);
            session.insert(result);
            session.fireAllRules();
            return result;
        } finally {
            session.dispose();
        }
    }

    /**
     * Execute receiving validation rules.
     */
    public ReceivingValidationResult validateReceiving(ReceivingValidationFacts facts) {
        KieSession session = kieContainer.newKieSession("inbound-rules-session");
        try {
            ReceivingValidationResult result = new ReceivingValidationResult();
            session.insert(facts);
            session.insert(result);
            session.fireAllRules();
            return result;
        } finally {
            session.dispose();
        }
    }

    /**
     * Execute quality inspection rules.
     */
    public QualityInspectionResult determineInspection(QualityInspectionFacts facts) {
        KieSession session = kieContainer.newKieSession("inbound-rules-session");
        try {
            QualityInspectionResult result = new QualityInspectionResult();
            session.insert(facts);
            session.insert(result);
            session.fireAllRules();
            return result;
        } finally {
            session.dispose();
        }
    }

    /**
     * Execute rules with multiple facts.
     */
    public void executeRules(String sessionName, Collection<Object> facts) {
        KieSession session = kieContainer.newKieSession(sessionName);
        try {
            facts.forEach(session::insert);
            session.fireAllRules();
        } finally {
            session.dispose();
        }
    }
}
