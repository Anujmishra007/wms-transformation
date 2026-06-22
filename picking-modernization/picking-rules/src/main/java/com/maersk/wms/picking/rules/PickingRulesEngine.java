package com.maersk.wms.picking.rules;

import com.maersk.wms.picking.domain.PickTask;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * Drools rules engine for picking business rules.
 *
 * Externalizes business logic from SPs into configurable rules:
 * - Task prioritization rules
 * - Short pick rules
 * - Zone assignment rules
 * - Batch optimization rules
 */
@Slf4j
@Service
public class PickingRulesEngine {

    private KieContainer kieContainer;

    @PostConstruct
    public void init() {
        try {
            KieServices kieServices = KieServices.Factory.get();
            kieContainer = kieServices.getKieClasspathContainer();
            log.info("Picking rules engine initialized");
        } catch (Exception e) {
            log.warn("Drools not fully configured - using fallback logic: {}", e.getMessage());
        }
    }

    /**
     * Apply task prioritization rules.
     */
    public List<PickTask> applyPrioritizationRules(List<PickTask> tasks) {
        if (kieContainer == null) {
            return tasks; // Fallback: no rules applied
        }

        KieSession session = null;
        try {
            session = kieContainer.newKieSession("pickingRulesSession");

            TaskPriorityFacts facts = TaskPriorityFacts.builder()
                    .tasks(tasks)
                    .build();

            session.insert(facts);
            session.fireAllRules();

            return facts.getPrioritizedTasks();
        } catch (Exception e) {
            log.error("Error applying prioritization rules", e);
            return tasks;
        } finally {
            if (session != null) {
                session.dispose();
            }
        }
    }

    /**
     * Evaluate short pick rules.
     */
    public ShortPickDecision evaluateShortPick(PickTask task, java.math.BigDecimal pickedQty) {
        if (kieContainer == null) {
            return ShortPickDecision.ALLOW; // Fallback
        }

        KieSession session = null;
        try {
            session = kieContainer.newKieSession("shortPickRulesSession");

            ShortPickFacts facts = ShortPickFacts.builder()
                    .task(task)
                    .pickedQty(pickedQty)
                    .requestedQty(task.getRequestedQty())
                    .build();

            session.insert(facts);
            session.fireAllRules();

            return facts.getDecision();
        } catch (Exception e) {
            log.error("Error evaluating short pick rules", e);
            return ShortPickDecision.ALLOW;
        } finally {
            if (session != null) {
                session.dispose();
            }
        }
    }

    /**
     * Apply zone assignment rules.
     */
    public String determineZone(PickTask task, String userId) {
        if (kieContainer == null) {
            return task.getZone(); // Fallback
        }

        KieSession session = null;
        try {
            session = kieContainer.newKieSession("zoneAssignmentRulesSession");

            ZoneAssignmentFacts facts = ZoneAssignmentFacts.builder()
                    .task(task)
                    .userId(userId)
                    .build();

            session.insert(facts);
            session.fireAllRules();

            return facts.getAssignedZone();
        } catch (Exception e) {
            log.error("Error applying zone assignment rules", e);
            return task.getZone();
        } finally {
            if (session != null) {
                session.dispose();
            }
        }
    }
}
