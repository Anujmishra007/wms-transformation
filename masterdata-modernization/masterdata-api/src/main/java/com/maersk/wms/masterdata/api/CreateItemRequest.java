package com.maersk.wms.masterdata.api;

import com.maersk.wms.masterdata.domain.Item;
import com.maersk.wms.masterdata.domain.ItemType;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * Request to create a new item.
 */
@Data
public class CreateItemRequest {

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotBlank(message = "Description is required")
    private String description;

    private String descriptionLong;
    private String itemType;
    private String itemGroup;
    private String itemClass;
    private String itemFamily;

    // Physical attributes
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private String dimensionUom;
    private BigDecimal weight;
    private String weightUom;
    private BigDecimal cube;

    // Storage
    private String storageType;
    private String storageZone;
    private String putawayZone;
    private String abcClass;

    // Control flags
    private boolean lotControlled;
    private boolean serialControlled;
    private boolean expirationControlled;
    private int shelfLife;
    private String rotationRule;

    // UOM
    private String baseUom;
    private BigDecimal baseQtyPerPack;
    private BigDecimal baseQtyPerCase;

    // Hazmat
    private boolean hazmat;
    private String hazmatClass;

    // Catch weight
    private boolean catchWeight;
    private BigDecimal minWeight;
    private BigDecimal maxWeight;

    public Item toEntity() {
        Item item = new Item();
        item.setSku(sku);
        item.setDescription(description);
        item.setDescriptionLong(descriptionLong);
        item.setItemType(itemType != null ? ItemType.valueOf(itemType) : ItemType.FINISHED_GOOD);
        item.setItemGroup(itemGroup);
        item.setItemClass(itemClass);
        item.setItemFamily(itemFamily);
        item.setLength(length);
        item.setWidth(width);
        item.setHeight(height);
        item.setDimensionUom(dimensionUom != null ? dimensionUom : "IN");
        item.setWeight(weight);
        item.setWeightUom(weightUom != null ? weightUom : "LB");
        item.setCube(cube);
        item.setStorageType(storageType);
        item.setStorageZone(storageZone);
        item.setPutawayZone(putawayZone);
        item.setAbcClass(abcClass);
        item.setLotControlled(lotControlled);
        item.setSerialControlled(serialControlled);
        item.setExpirationControlled(expirationControlled);
        item.setShelfLife(shelfLife);
        item.setRotationRule(rotationRule);
        item.setBaseUom(baseUom != null ? baseUom : "EA");
        item.setBaseQtyPerPack(baseQtyPerPack);
        item.setBaseQtyPerCase(baseQtyPerCase);
        item.setHazmat(hazmat);
        item.setHazmatClass(hazmatClass);
        item.setCatchWeight(catchWeight);
        item.setMinWeight(minWeight);
        item.setMaxWeight(maxWeight);
        return item;
    }
}
