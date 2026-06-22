package com.maersk.wms.inventory.domain.saga;

import com.maersk.wms.inventory.domain.core.service.InventoryCoreService;
import com.maersk.wms.inventory.domain.saga.InventorySagaEvents;
import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Saga orchestrator for inventory allocation operations.
 * Implements compensation pattern for distributed transactions.
 *
 * Flow:
 * 1. Reserve inventory (soft allocation)
 * 2. Create pick tasks
 * 3. Confirm allocation (hard allocation)
 *
 * Compensation:
 * - On failure, release reserved inventory
 * - Cancel any created pick tasks
 * - Publish AllocationFailed event
 */
@Component
public class InventoryAllocationSaga {

    private static final Logger log = LoggerFactory.getLogger(InventoryAllocationSaga.class);

    private final InventoryCoreService inventoryCoreService;
    private final ApplicationEventPublisher eventPublisher;

    public InventoryAllocationSaga(InventoryCoreService inventoryCoreService,
                                    ApplicationEventPublisher eventPublisher) {
        this.inventoryCoreService = inventoryCoreService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Execute allocation saga.
     */
    @Transactional
    public AllocationSagaResult execute(AllocationCriteria criteria) {
        String sagaId = UUID.randomUUID().toString();
        List<CompensationAction> compensationActions = new ArrayList<>();

        log.info("Starting allocation saga: sagaId={}, sku={}, order={}",
                sagaId, criteria.skuKey(), criteria.orderKey());

        // Publish saga started event
        eventPublisher.publishEvent(new InventorySagaEvents.SagaStarted(
                sagaId,
                "ALLOCATION",
                criteria.orderKey().value(),
                Instant.now()
        ));

        try {
            // Step 1: Allocate inventory
            AllocationResult allocationResult = inventoryCoreService.allocateInventory(criteria);

            if (!allocationResult.fullyAllocated()) {
                // Partial allocation - might need compensation based on business rules
                log.warn("Partial allocation: sagaId={}, allocated={}, shortage={}",
                        sagaId, allocationResult.allocatedQuantity(), allocationResult.shortageQuantity());
            }

            // Record compensation action for deallocation
            compensationActions.add(new CompensationAction(
                    "DEALLOCATE",
                    allocationResult.allocationKey(),
                    allocationResult.allocatedQuantity()
            ));

            // Step 2: Publish allocation saga step completed
            eventPublisher.publishEvent(new InventorySagaEvents.AllocationSagaStep(
                    sagaId,
                    "INVENTORY_ALLOCATED",
                    allocationResult.allocationKey(),
                    allocationResult.allocatedQuantity(),
                    criteria.skuKey(),
                    criteria.orderKey(),
                    Instant.now()
            ));

            // Publish saga completed
            eventPublisher.publishEvent(new InventorySagaEvents.SagaCompleted(
                    sagaId,
                    "ALLOCATION",
                    true,
                    null,
                    Instant.now()
            ));

            log.info("Allocation saga completed successfully: sagaId={}, allocationKey={}",
                    sagaId, allocationResult.allocationKey());

            return new AllocationSagaResult(
                    sagaId,
                    allocationResult,
                    true,
                    null,
                    compensationActions
            );

        } catch (Exception e) {
            log.error("Allocation saga failed: sagaId={}", sagaId, e);

            // Execute compensation
            executeCompensation(sagaId, compensationActions, criteria.allocatedBy());

            // Publish saga failed
            eventPublisher.publishEvent(new InventorySagaEvents.SagaCompleted(
                    sagaId,
                    "ALLOCATION",
                    false,
                    e.getMessage(),
                    Instant.now()
            ));

            return new AllocationSagaResult(
                    sagaId,
                    null,
                    false,
                    e.getMessage(),
                    compensationActions
            );
        }
    }

    /**
     * Execute compensation actions in reverse order.
     */
    private void executeCompensation(String sagaId, List<CompensationAction> actions, UserKey user) {
        log.info("Executing compensation: sagaId={}, actions={}", sagaId, actions.size());

        // Execute in reverse order
        for (int i = actions.size() - 1; i >= 0; i--) {
            CompensationAction action = actions.get(i);

            try {
                switch (action.actionType()) {
                    case "DEALLOCATE" -> {
                        inventoryCoreService.deallocateInventory(
                                action.allocationKey(),
                                "Saga compensation: " + sagaId,
                                user
                        );
                        log.debug("Compensation executed: deallocated {}", action.allocationKey());
                    }
                    default -> log.warn("Unknown compensation action type: {}", action.actionType());
                }

                // Publish compensation step event
                eventPublisher.publishEvent(new InventorySagaEvents.CompensationStepEvent(
                        sagaId,
                        action.actionType(),
                        action.allocationKey() != null ? action.allocationKey().value() : null,
                        true,
                        null,
                        Instant.now()
                ));

            } catch (Exception compensationError) {
                log.error("Compensation action failed: sagaId={}, action={}",
                        sagaId, action.actionType(), compensationError);

                // Publish compensation failure
                eventPublisher.publishEvent(new InventorySagaEvents.CompensationStepEvent(
                        sagaId,
                        action.actionType(),
                        action.allocationKey() != null ? action.allocationKey().value() : null,
                        false,
                        compensationError.getMessage(),
                        Instant.now()
                ));

                // Continue with other compensations
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // RESULT TYPES
    // ═══════════════════════════════════════════════════════════════

    public record AllocationSagaResult(
            String sagaId,
            AllocationResult allocationResult,
            boolean success,
            String errorMessage,
            List<CompensationAction> compensationActions
    ) {}

    public record CompensationAction(
            String actionType,
            AllocationKey allocationKey,
            Quantity quantity
    ) {}
}
