package com.maersk.wms.masterdata.service;

import com.maersk.wms.masterdata.domain.Item;
import com.maersk.wms.masterdata.domain.ItemStatus;
import com.maersk.wms.masterdata.domain.repository.ItemRepository;
import com.maersk.wms.masterdata.plugin.ItemPlugin;
import com.maersk.wms.masterdata.plugin.MasterDataPluginContext;
import com.maersk.wms.masterdata.plugin.MasterDataPluginRegistry;
import com.maersk.wms.masterdata.plugin.PluginResult;
import com.maersk.wms.masterdata.rules.ItemValidationFacts;
import com.maersk.wms.masterdata.rules.ItemValidationResult;
import com.maersk.wms.masterdata.rules.MasterDataRulesEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for item/SKU master data operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final MasterDataPluginRegistry pluginRegistry;
    private final MasterDataRulesEngine rulesEngine;

    /**
     * Create a new item.
     */
    @Transactional
    public Item createItem(Item item, MasterDataPluginContext context) {
        log.info("Creating item: {} for client: {}", item.getSku(), context.getClientCode());

        // Check if SKU already exists
        if (itemRepository.existsBySku(item.getSku())) {
            throw new MasterDataOperationException("Item with SKU already exists: " + item.getSku());
        }

        // Execute before create plugins
        PluginResult beforeResult = pluginRegistry.executeAll(
                ItemPlugin.class,
                context,
                plugin -> plugin.beforeItemCreate(item, context)
        );

        if (!beforeResult.isSuccess()) {
            throw new MasterDataOperationException("Item creation blocked: " + beforeResult.getErrorMessage());
        }

        // Validate with rules engine
        ItemValidationFacts facts = buildValidationFacts(item, context, "CREATE");
        ItemValidationResult validationResult = rulesEngine.evaluateItemRules(facts);

        if (!validationResult.isValid()) {
            throw new MasterDataOperationException(
                    "Item validation failed: " + String.join(", ", validationResult.getErrors()));
        }

        // Apply computed values from rules
        if (validationResult.getComputedAbcClass() != null) {
            item.setAbcClass(validationResult.getComputedAbcClass());
        }
        if (validationResult.getComputedStorageZone() != null) {
            item.setStorageZone(validationResult.getComputedStorageZone());
        }

        // Transform through plugin
        Optional<ItemPlugin> itemPlugin = pluginRegistry.getPlugin(ItemPlugin.class, context);
        Item transformedItem = itemPlugin.map(p -> p.transformItem(item, context)).orElse(item);

        // Set audit fields
        transformedItem.setStatus(ItemStatus.ACTIVE);
        transformedItem.setCreatedBy(context.getUserId());
        transformedItem.setCreatedAt(LocalDateTime.now());

        // Save
        Item savedItem = itemRepository.save(transformedItem);

        // Execute after create plugins
        pluginRegistry.executeAll(
                ItemPlugin.class,
                context,
                plugin -> plugin.afterItemCreate(savedItem, context)
        );

        log.info("Item created: {}", savedItem.getSku());
        return savedItem;
    }

    /**
     * Update an existing item.
     */
    @Transactional
    public Item updateItem(String sku, Item updates, MasterDataPluginContext context) {
        log.info("Updating item: {}", sku);

        Item existingItem = itemRepository.findBySku(sku)
                .orElseThrow(() -> new MasterDataOperationException("Item not found: " + sku));

        // Execute before update plugins
        PluginResult beforeResult = pluginRegistry.executeAll(
                ItemPlugin.class,
                context,
                plugin -> plugin.beforeItemUpdate(updates, context)
        );

        if (!beforeResult.isSuccess()) {
            throw new MasterDataOperationException("Item update blocked: " + beforeResult.getErrorMessage());
        }

        // Merge updates
        mergeUpdates(existingItem, updates);

        // Validate
        ItemValidationFacts facts = buildValidationFacts(existingItem, context, "UPDATE");
        ItemValidationResult validationResult = rulesEngine.evaluateItemRules(facts);

        if (!validationResult.isValid()) {
            throw new MasterDataOperationException(
                    "Item validation failed: " + String.join(", ", validationResult.getErrors()));
        }

        // Set audit fields
        existingItem.setUpdatedBy(context.getUserId());
        existingItem.setUpdatedAt(LocalDateTime.now());

        // Save
        Item savedItem = itemRepository.save(existingItem);

        // Execute after update plugins
        pluginRegistry.executeAll(
                ItemPlugin.class,
                context,
                plugin -> plugin.afterItemUpdate(savedItem, context)
        );

        log.info("Item updated: {}", savedItem.getSku());
        return savedItem;
    }

    /**
     * Get item by SKU.
     */
    public Optional<Item> getItem(String sku) {
        return itemRepository.findBySku(sku);
    }

    /**
     * Search items by SKU or description.
     */
    public List<Item> searchItems(String searchTerm) {
        return itemRepository.searchBySkuOrDescription(searchTerm);
    }

    /**
     * Get items by status.
     */
    public List<Item> getItemsByStatus(ItemStatus status) {
        return itemRepository.findByStatus(status);
    }

    private void mergeUpdates(Item existing, Item updates) {
        if (updates.getDescription() != null) existing.setDescription(updates.getDescription());
        if (updates.getItemType() != null) existing.setItemType(updates.getItemType());
        if (updates.getLength() != null) existing.setLength(updates.getLength());
        if (updates.getWidth() != null) existing.setWidth(updates.getWidth());
        if (updates.getHeight() != null) existing.setHeight(updates.getHeight());
        if (updates.getWeight() != null) existing.setWeight(updates.getWeight());
        if (updates.getStorageZone() != null) existing.setStorageZone(updates.getStorageZone());
        // Add more fields as needed
    }

    private ItemValidationFacts buildValidationFacts(Item item, MasterDataPluginContext context, String operation) {
        return ItemValidationFacts.builder()
                .clientCode(context.getClientCode())
                .facilityCode(context.getFacilityCode())
                .operationType(operation)
                .sku(item.getSku())
                .description(item.getDescription())
                .itemType(item.getItemType() != null ? item.getItemType().name() : null)
                .itemGroup(item.getItemGroup())
                .itemClass(item.getItemClass())
                .length(item.getLength())
                .width(item.getWidth())
                .height(item.getHeight())
                .weight(item.getWeight())
                .cube(item.getCube())
                .lotControlled(item.isLotControlled())
                .serialControlled(item.isSerialControlled())
                .expirationControlled(item.isExpirationControlled())
                .shelfLife(item.getShelfLife())
                .storageType(item.getStorageType())
                .storageZone(item.getStorageZone())
                .hazmat(item.isHazmat())
                .hazmatClass(item.getHazmatClass())
                .catchWeight(item.isCatchWeight())
                .minWeight(item.getMinWeight())
                .maxWeight(item.getMaxWeight())
                .clientConfig(context.getParameters())
                .build();
    }
}
