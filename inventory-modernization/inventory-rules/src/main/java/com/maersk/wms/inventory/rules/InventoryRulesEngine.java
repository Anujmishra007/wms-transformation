package com.maersk.wms.inventory.rules;

import com.maersk.wms.inventory.domain.LotxLocxId;
import com.maersk.wms.inventory.domain.InventoryAdjustment;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * Drools rules engine for inventory business rules.
 *
 * Externalizes business logic:
 * - FIFO allocation sorting (20+ variants)
 * - Adjustment approval thresholds
 * - Hold/release rules
 * - Expiry management rules
 */
@Slf4j
@Service
public class InventoryRulesEngine {

    private KieContainer kieContainer;

    @PostConstruct
    public void init() {
        try {
            KieServices kieServices = KieServices.Factory.get();
            kieContainer = kieServices.getKieClasspathContainer();
            log.info("Inventory rules engine initialized");
        } catch (Exception e) {
            log.warn("Drools not fully configured - using fallback logic: {}", e.getMessage());
        }
    }

    /**
     * Apply FIFO allocation rules to sort inventory.
     */
    public List<LotxLocxId> applyAllocationRules(List<LotxLocxId> inventory, String fifoVariant) {
        if (kieContainer == null) {
            return inventory;
        }

        KieSession session = null;
        try {
            session = kieContainer.newKieSession("allocationRulesSession");

            AllocationFacts facts = AllocationFacts.builder()
                    .inventory(inventory)
                    .fifoVariant(fifoVariant)
                    .build();

            session.insert(facts);
            session.fireAllRules();

            return facts.getSortedInventory();
        } catch (Exception e) {
            log.error("Error applying allocation rules", e);
            return inventory;
        } finally {
            if (session != null) session.dispose();
        }
    }

    /**
     * Evaluate adjustment approval rules.
     */
    public AdjustmentApprovalResult evaluateAdjustmentApproval(InventoryAdjustment adjustment) {
        if (kieContainer == null) {
            return AdjustmentApprovalResult.builder()
                    .requiresApproval(adjustment.getAdjustmentType().requiresApproval())
                    .build();
        }

        KieSession session = null;
        try {
            session = kieContainer.newKieSession("adjustmentRulesSession");

            AdjustmentApprovalFacts facts = AdjustmentApprovalFacts.builder()
                    .adjustment(adjustment)
                    .build();

            session.insert(facts);
            session.fireAllRules();

            return facts.getResult();
        } catch (Exception e) {
            log.error("Error evaluating adjustment approval", e);
            return AdjustmentApprovalResult.builder()
                    .requiresApproval(true)
                    .reason("Error evaluating rules")
                    .build();
        } finally {
            if (session != null) session.dispose();
        }
    }

    /**
     * Check expiry rules.
     */
    public ExpiryCheckResult checkExpiryRules(LotxLocxId inventory) {
        if (kieContainer == null) {
            return ExpiryCheckResult.builder().isValid(true).build();
        }

        KieSession session = null;
        try {
            session = kieContainer.newKieSession("expiryRulesSession");

            ExpiryFacts facts = ExpiryFacts.builder()
                    .inventory(inventory)
                    .build();

            session.insert(facts);
            session.fireAllRules();

            return facts.getResult();
        } catch (Exception e) {
            log.error("Error checking expiry rules", e);
            return ExpiryCheckResult.builder().isValid(true).build();
        } finally {
            if (session != null) session.dispose();
        }
    }
}
