package com.maersk.wms.inventory.domain.saga;

import com.maersk.wms.inventory.domain.core.model.Inventory;
import com.maersk.wms.inventory.domain.core.repository.InventoryRepository;
import com.maersk.wms.inventory.domain.events.saga.InventorySagaEvents;
import com.maersk.wms.inventory.domain.events.MovementEvents;
import com.maersk.wms.inventory.shared.kernel.exceptions.*;
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
 * Saga orchestrator for inventory transfer operations.
 * Implements compensation pattern for location-to-location transfers.
 *
 * Flow:
 * 1. Validate source inventory exists and has sufficient quantity
 * 2. Deduct from source location
 * 3. Add to destination location
 * 4. Update transaction history
 *
 * Compensation:
 * - On failure after source deduction, reverse the deduction
 * - Publish TransferFailed event
 */
@Component
public class InventoryTransferSaga {

    private static final Logger log = LoggerFactory.getLogger(InventoryTransferSaga.class);

    private final InventoryRepository inventoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    public InventoryTransferSaga(InventoryRepository inventoryRepository,
                                  ApplicationEventPublisher eventPublisher) {
        this.inventoryRepository = inventoryRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Execute transfer saga.
     */
    @Transactional
    public TransferSagaResult execute(TransferRequest request) {
        String sagaId = UUID.randomUUID().toString();
        List<CompensationAction> compensationActions = new ArrayList<>();

        log.info("Starting transfer saga: sagaId={}, source={}, destination={}",
                sagaId, request.fromLocation(), request.toLocation());

        // Publish saga started
        eventPublisher.publishEvent(new InventorySagaEvents.SagaStarted(
                sagaId,
                "TRANSFER",
                request.inventoryKey().value(),
                Instant.now()
        ));

        try {
            // Step 1: Validate and lock source inventory
            Inventory sourceInventory = inventoryRepository.findByKeyForUpdate(request.inventoryKey())
                    .orElseThrow(() -> new InventoryNotFoundException(request.inventoryKey()));

            if (sourceInventory.availableQuantity().isLessThan(request.quantity())) {
                throw new InsufficientInventoryException(
                        request.inventoryKey(),
                        request.quantity(),
                        sourceInventory.availableQuantity()
                );
            }

            // Step 2: Deduct from source
            Quantity previousSourceQuantity = sourceInventory.onHandQuantity();
            sourceInventory.updateQuantity(
                    previousSourceQuantity.subtract(request.quantity()),
                    "Transfer to " + request.toLocation()
            );
            inventoryRepository.save(sourceInventory);

            // Record compensation for source deduction
            compensationActions.add(new CompensationAction(
                    "RESTORE_SOURCE",
                    request.inventoryKey(),
                    request.fromLocation(),
                    request.quantity(),
                    previousSourceQuantity
            ));

            // Publish step completed
            eventPublisher.publishEvent(new InventorySagaEvents.TransferSagaStep(
                    sagaId,
                    "SOURCE_DEDUCTED",
                    request.inventoryKey(),
                    request.fromLocation(),
                    request.toLocation(),
                    request.quantity(),
                    Instant.now()
            ));

            // Step 3: Add to destination (or create new inventory record)
            InventoryKey destinationKey = addToDestination(request, sourceInventory);

            // Record compensation for destination addition
            compensationActions.add(new CompensationAction(
                    "REMOVE_DESTINATION",
                    destinationKey,
                    request.toLocation(),
                    request.quantity(),
                    null
            ));

            // Publish step completed
            eventPublisher.publishEvent(new InventorySagaEvents.TransferSagaStep(
                    sagaId,
                    "DESTINATION_ADDED",
                    destinationKey,
                    request.fromLocation(),
                    request.toLocation(),
                    request.quantity(),
                    Instant.now()
            ));

            // Publish transfer completed event
            eventPublisher.publishEvent(new MovementEvents.InventoryTransferred(
                    request.inventoryKey(),
                    sourceInventory.skuKey(),
                    sourceInventory.lotKey(),
                    request.fromLocation(),
                    request.toLocation(),
                    request.quantity(),
                    request.reason(),
                    request.transferredBy(),
                    Instant.now()
            ));

            // Publish saga completed
            eventPublisher.publishEvent(new InventorySagaEvents.SagaCompleted(
                    sagaId,
                    "TRANSFER",
                    true,
                    null,
                    Instant.now()
            ));

            log.info("Transfer saga completed successfully: sagaId={}", sagaId);

            return new TransferSagaResult(
                    sagaId,
                    request.inventoryKey(),
                    destinationKey,
                    true,
                    null,
                    compensationActions
            );

        } catch (Exception e) {
            log.error("Transfer saga failed: sagaId={}", sagaId, e);

            // Execute compensation
            executeCompensation(sagaId, compensationActions);

            // Publish saga failed
            eventPublisher.publishEvent(new InventorySagaEvents.SagaCompleted(
                    sagaId,
                    "TRANSFER",
                    false,
                    e.getMessage(),
                    Instant.now()
            ));

            return new TransferSagaResult(
                    sagaId,
                    request.inventoryKey(),
                    null,
                    false,
                    e.getMessage(),
                    compensationActions
            );
        }
    }

    private InventoryKey addToDestination(TransferRequest request, Inventory sourceInventory) {
        // Check if inventory already exists at destination with same lot/lpn
        var existingDestination = inventoryRepository.findByCompositeKey(
                sourceInventory.lotKey(),
                request.toLocation(),
                sourceInventory.lpnKey()
        );

        if (existingDestination.isPresent()) {
            // Add to existing inventory
            Inventory destination = existingDestination.get();
            destination.updateQuantity(
                    destination.onHandQuantity().add(request.quantity()),
                    "Transfer from " + request.fromLocation()
            );
            inventoryRepository.save(destination);
            return destination.inventoryKey();
        } else {
            // Create new inventory at destination
            Inventory newDestination = Inventory.builder()
                    .inventoryKey(new InventoryKey(UUID.randomUUID().toString()))
                    .skuKey(sourceInventory.skuKey())
                    .lotKey(sourceInventory.lotKey())
                    .locationKey(request.toLocation())
                    .lpnKey(sourceInventory.lpnKey())
                    .storerKey(sourceInventory.storerKey())
                    .warehouseKey(sourceInventory.warehouseKey())
                    .onHandQuantity(request.quantity())
                    .lottables(sourceInventory.lottables())
                    .status(sourceInventory.status())
                    .build();
            newDestination = inventoryRepository.save(newDestination);
            return newDestination.inventoryKey();
        }
    }

    private void executeCompensation(String sagaId, List<CompensationAction> actions) {
        log.info("Executing transfer compensation: sagaId={}, actions={}", sagaId, actions.size());

        // Execute in reverse order
        for (int i = actions.size() - 1; i >= 0; i--) {
            CompensationAction action = actions.get(i);

            try {
                switch (action.actionType()) {
                    case "RESTORE_SOURCE" -> {
                        Inventory source = inventoryRepository.findByKeyForUpdate(action.inventoryKey())
                                .orElse(null);
                        if (source != null) {
                            source.updateQuantity(action.previousQuantity(), "Compensation: restore source");
                            inventoryRepository.save(source);
                        }
                        log.debug("Compensation executed: restored source {}", action.inventoryKey());
                    }
                    case "REMOVE_DESTINATION" -> {
                        Inventory destination = inventoryRepository.findByKeyForUpdate(action.inventoryKey())
                                .orElse(null);
                        if (destination != null) {
                            Quantity newQty = destination.onHandQuantity().subtract(action.quantity());
                            if (newQty.isZeroOrNegative()) {
                                inventoryRepository.delete(destination);
                            } else {
                                destination.updateQuantity(newQty, "Compensation: remove destination");
                                inventoryRepository.save(destination);
                            }
                        }
                        log.debug("Compensation executed: removed from destination {}", action.inventoryKey());
                    }
                    default -> log.warn("Unknown compensation action type: {}", action.actionType());
                }

                // Publish compensation step
                eventPublisher.publishEvent(new InventorySagaEvents.CompensationStep(
                        sagaId,
                        action.actionType(),
                        action.inventoryKey().value(),
                        true,
                        null,
                        Instant.now()
                ));

            } catch (Exception compensationError) {
                log.error("Compensation action failed: sagaId={}, action={}",
                        sagaId, action.actionType(), compensationError);

                eventPublisher.publishEvent(new InventorySagaEvents.CompensationStep(
                        sagaId,
                        action.actionType(),
                        action.inventoryKey().value(),
                        false,
                        compensationError.getMessage(),
                        Instant.now()
                ));
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // REQUEST/RESULT TYPES
    // ═══════════════════════════════════════════════════════════════

    public record TransferRequest(
            InventoryKey inventoryKey,
            LocationKey fromLocation,
            LocationKey toLocation,
            Quantity quantity,
            String reason,
            UserKey transferredBy
    ) {}

    public record TransferSagaResult(
            String sagaId,
            InventoryKey sourceInventoryKey,
            InventoryKey destinationInventoryKey,
            boolean success,
            String errorMessage,
            List<CompensationAction> compensationActions
    ) {}

    public record CompensationAction(
            String actionType,
            InventoryKey inventoryKey,
            LocationKey location,
            Quantity quantity,
            Quantity previousQuantity
    ) {}
}
