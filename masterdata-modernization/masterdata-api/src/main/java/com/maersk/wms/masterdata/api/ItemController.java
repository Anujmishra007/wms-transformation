package com.maersk.wms.masterdata.api;

import com.maersk.wms.masterdata.domain.Item;
import com.maersk.wms.masterdata.domain.ItemStatus;
import com.maersk.wms.masterdata.plugin.MasterDataPluginContext;
import com.maersk.wms.masterdata.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST API for item/SKU master data operations.
 */
@RestController
@RequestMapping("/api/v1/masterdata/items")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Items", description = "Item/SKU master data APIs")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @Operation(summary = "Create a new item")
    public ResponseEntity<ItemResponse> createItem(
            @Valid @RequestBody CreateItemRequest request,
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestHeader("X-Facility-Code") String facilityCode,
            @RequestHeader("X-User-Id") String userId) {

        MasterDataPluginContext context = MasterDataPluginContext.builder()
                .clientCode(clientCode)
                .facilityCode(facilityCode)
                .userId(userId)
                .build();

        Item item = request.toEntity();
        Item created = itemService.createItem(item, context);

        return ResponseEntity.ok(ItemResponse.fromEntity(created));
    }

    @GetMapping("/{sku}")
    @Operation(summary = "Get item by SKU")
    public ResponseEntity<ItemResponse> getItem(@PathVariable String sku) {
        return itemService.getItem(sku)
                .map(ItemResponse::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{sku}")
    @Operation(summary = "Update an existing item")
    public ResponseEntity<ItemResponse> updateItem(
            @PathVariable String sku,
            @Valid @RequestBody UpdateItemRequest request,
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestHeader("X-Facility-Code") String facilityCode,
            @RequestHeader("X-User-Id") String userId) {

        MasterDataPluginContext context = MasterDataPluginContext.builder()
                .clientCode(clientCode)
                .facilityCode(facilityCode)
                .userId(userId)
                .build();

        Item updates = request.toEntity();
        Item updated = itemService.updateItem(sku, updates, context);

        return ResponseEntity.ok(ItemResponse.fromEntity(updated));
    }

    @GetMapping
    @Operation(summary = "Search items")
    public ResponseEntity<List<ItemResponse>> searchItems(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ItemStatus status) {

        List<Item> items;
        if (search != null && !search.isEmpty()) {
            items = itemService.searchItems(search);
        } else if (status != null) {
            items = itemService.getItemsByStatus(status);
        } else {
            items = itemService.getItemsByStatus(ItemStatus.ACTIVE);
        }

        return ResponseEntity.ok(items.stream()
                .map(ItemResponse::fromEntity)
                .collect(Collectors.toList()));
    }
}
