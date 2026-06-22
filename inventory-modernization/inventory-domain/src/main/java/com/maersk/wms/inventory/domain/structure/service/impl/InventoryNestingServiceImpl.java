package com.maersk.wms.inventory.domain.structure.service.impl;

import com.maersk.wms.inventory.domain.structure.model.InventoryHierarchy;
import com.maersk.wms.inventory.domain.structure.repository.InventoryHierarchyRepository;
import com.maersk.wms.inventory.domain.structure.service.InventoryNestingService;
import com.maersk.wms.inventory.domain.events.NestingEvents;
import com.maersk.wms.inventory.shared.kernel.exceptions.*;
import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for inventory nesting/hierarchy operations.
 * Manages parent-child relationships: Pallet → Case → Inner Pack → Each.
 */
@Service
@Transactional
public class InventoryNestingServiceImpl implements InventoryNestingService {

    private final InventoryHierarchyRepository hierarchyRepository;
    private final ApplicationEventPublisher eventPublisher;

    public InventoryNestingServiceImpl(InventoryHierarchyRepository hierarchyRepository,
                                        ApplicationEventPublisher eventPublisher) {
        this.hierarchyRepository = hierarchyRepository;
        this.eventPublisher = eventPublisher;
    }

    // ═══════════════════════════════════════════════════════════════
    // NESTING OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    @Override
    public InventoryHierarchy nest(LpnKey parentLpn, InventoryHierarchy.ContainerType parentType,
                                    LpnKey childLpn, InventoryHierarchy.ContainerType childType,
                                    int quantity, UserKey nestedBy, WarehouseKey warehouseKey) {
        // Validate nesting is allowed
        if (!canNest(parentLpn, childLpn)) {
            throw new NestingException("Cannot nest " + childLpn + " under " + parentLpn);
        }

        // Check for cycles
        if (wouldCreateCycle(parentLpn, childLpn)) {
            throw new NestingException("Nesting would create a cycle");
        }

        // Validate container type hierarchy
        validateContainerTypeHierarchy(parentType, childType);

        // Check if already nested
        if (hierarchyRepository.isNested(childLpn)) {
            throw new NestingException("Child LPN " + childLpn + " is already nested");
        }

        InventoryHierarchy hierarchy = InventoryHierarchy.builder()
                .nestingKey(new NestingKey(UUID.randomUUID().toString()))
                .parentLpnKey(parentLpn)
                .parentType(parentType)
                .childLpnKey(childLpn)
                .childType(childType)
                .quantity(quantity)
                .warehouseKey(warehouseKey)
                .nestedBy(nestedBy)
                .nestedAt(Instant.now())
                .status(InventoryHierarchy.NestingStatus.ACTIVE)
                .build();

        hierarchy = hierarchyRepository.save(hierarchy);

        // Publish event
        eventPublisher.publishEvent(new NestingEvents.InventoryNested(
                hierarchy.nestingKey(),
                parentLpn,
                parentType,
                childLpn,
                childType,
                quantity,
                warehouseKey,
                nestedBy,
                Instant.now()
        ));

        return hierarchy;
    }

    @Override
    public void unnest(LpnKey parentLpn, LpnKey childLpn, String reason,
                        LocationKey newLocation, UserKey unnestedBy) {
        InventoryHierarchy hierarchy = hierarchyRepository.findByParentAndChild(parentLpn, childLpn)
                .orElseThrow(() -> new NestingException(
                        "No nesting relationship found between " + parentLpn + " and " + childLpn));

        hierarchyRepository.delete(hierarchy.nestingKey());

        // Publish event
        eventPublisher.publishEvent(new NestingEvents.InventoryUnnested(
                hierarchy.nestingKey(),
                parentLpn,
                childLpn,
                reason,
                newLocation,
                unnestedBy,
                Instant.now()
        ));
    }

    @Override
    public InventoryHierarchy buildPallet(LpnKey palletLpn, List<LpnKey> caseLpns,
                                           LocationKey locationKey, UserKey builtBy,
                                           WarehouseKey warehouseKey) {
        // Validate all cases are not already nested
        for (LpnKey caseLpn : caseLpns) {
            if (hierarchyRepository.isNested(caseLpn)) {
                throw new NestingException("Case " + caseLpn + " is already nested on another pallet");
            }
        }

        // Create hierarchy records for each case
        List<InventoryHierarchy> hierarchies = new ArrayList<>();
        for (LpnKey caseLpn : caseLpns) {
            InventoryHierarchy hierarchy = nest(
                    palletLpn, InventoryHierarchy.ContainerType.PALLET,
                    caseLpn, InventoryHierarchy.ContainerType.CASE,
                    1, builtBy, warehouseKey
            );
            hierarchies.add(hierarchy);
        }

        // Publish pallet built event
        eventPublisher.publishEvent(new NestingEvents.PalletBuilt(
                palletLpn,
                caseLpns,
                caseLpns.size(),
                locationKey,
                warehouseKey,
                builtBy,
                Instant.now()
        ));

        // Return the first hierarchy (pallet record)
        return hierarchies.isEmpty() ? null : hierarchies.get(0);
    }

    @Override
    public List<LpnKey> breakPallet(LpnKey palletLpn, String reason, UserKey brokenBy) {
        List<LpnKey> caseLpns = hierarchyRepository.findChildren(palletLpn);

        if (caseLpns.isEmpty()) {
            throw new NestingException("Pallet " + palletLpn + " has no nested cases");
        }

        // Remove all nesting relationships
        for (LpnKey caseLpn : caseLpns) {
            hierarchyRepository.deleteByParentAndChild(palletLpn, caseLpn);
        }

        // Publish pallet broken event
        eventPublisher.publishEvent(new NestingEvents.PalletBroken(
                palletLpn,
                caseLpns,
                reason,
                brokenBy,
                Instant.now()
        ));

        return caseLpns;
    }

    @Override
    public LpnKey consolidate(List<LpnKey> sourceLpns, LpnKey targetLpn,
                               UserKey consolidatedBy, WarehouseKey warehouseKey) {
        // Validate source LPNs exist and are not nested
        for (LpnKey sourceLpn : sourceLpns) {
            if (hierarchyRepository.hasChildren(sourceLpn)) {
                throw new NestingException("Cannot consolidate LPN " + sourceLpn + " that has children");
            }
        }

        // This is a simplified consolidation - in practice, this would involve
        // moving inventory from source LPNs to target LPN
        // For hierarchy purposes, we might nest source LPNs under target

        // Publish consolidation event
        eventPublisher.publishEvent(new NestingEvents.InventoryConsolidated(
                sourceLpns,
                targetLpn,
                warehouseKey,
                consolidatedBy,
                Instant.now()
        ));

        return targetLpn;
    }

    // ═══════════════════════════════════════════════════════════════
    // QUERY OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public Optional<LpnKey> getParent(LpnKey childLpn) {
        return hierarchyRepository.findParent(childLpn);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LpnKey> getChildren(LpnKey parentLpn) {
        return hierarchyRepository.findChildren(parentLpn);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryHierarchy> getHierarchy(LpnKey lpnKey) {
        return hierarchyRepository.findCompleteHierarchy(lpnKey);
    }

    @Override
    @Transactional(readOnly = true)
    public LpnKey getRootContainer(LpnKey lpnKey) {
        return hierarchyRepository.findRootContainer(lpnKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LpnKey> getLeafLpns(LpnKey parentLpn) {
        return hierarchyRepository.findLeafLpns(parentLpn);
    }

    @Override
    @Transactional(readOnly = true)
    public int getNestingLevel(LpnKey lpnKey) {
        return hierarchyRepository.getNestingLevel(lpnKey);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isNested(LpnKey lpnKey) {
        return hierarchyRepository.isNested(lpnKey);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasChildren(LpnKey lpnKey) {
        return hierarchyRepository.hasChildren(lpnKey);
    }

    // ═══════════════════════════════════════════════════════════════
    // VALIDATION OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public boolean canNest(LpnKey parentLpn, LpnKey childLpn) {
        // Cannot nest if same LPN
        if (parentLpn.equals(childLpn)) {
            return false;
        }

        // Cannot nest if child is already nested
        if (hierarchyRepository.isNested(childLpn)) {
            return false;
        }

        // Cannot nest if would create cycle
        if (wouldCreateCycle(parentLpn, childLpn)) {
            return false;
        }

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean wouldCreateCycle(LpnKey parentLpn, LpnKey childLpn) {
        return hierarchyRepository.wouldCreateCycle(parentLpn, childLpn);
    }

    // ═══════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ═══════════════════════════════════════════════════════════════

    private void validateContainerTypeHierarchy(InventoryHierarchy.ContainerType parentType,
                                                 InventoryHierarchy.ContainerType childType) {
        // Validate proper nesting hierarchy: PALLET > CASE > INNER_PACK > EACH
        int parentLevel = getContainerLevel(parentType);
        int childLevel = getContainerLevel(childType);

        if (parentLevel >= childLevel) {
            throw new NestingException(
                    "Invalid container hierarchy: " + parentType + " cannot contain " + childType);
        }
    }

    private int getContainerLevel(InventoryHierarchy.ContainerType type) {
        return switch (type) {
            case PALLET -> 1;
            case TOTE -> 1;
            case CASE -> 2;
            case INNER_PACK -> 3;
            case EACH -> 4;
            case MIXED -> 2;
        };
    }
}
