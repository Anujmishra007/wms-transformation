package com.maersk.wms.masterdata.api;

import com.maersk.wms.masterdata.domain.Item;
import com.maersk.wms.masterdata.domain.ItemStatus;
import com.maersk.wms.masterdata.domain.ItemType;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Request to update an existing item.
 */
@Data
public class UpdateItemRequest {

    private String description;
    private String descriptionLong;
    private String itemType;
    private String status;
    private String itemGroup;
    private String itemClass;

    // Physical attributes
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal weight;
    private BigDecimal cube;

    // Storage
    private String storageType;
    private String storageZone;
    private String abcClass;

    // Control flags
    private Boolean lotControlled;
    private Boolean expirationControlled;
    private Integer shelfLife;

    // Hazmat
    private Boolean hazmat;
    private String hazmatClass;

    public Item toEntity() {
        Item item = new Item();
        item.setDescription(description);
        item.setDescriptionLong(descriptionLong);
        if (itemType != null) item.setItemType(ItemType.valueOf(itemType));
        if (status != null) item.setStatus(ItemStatus.valueOf(status));
        item.setItemGroup(itemGroup);
        item.setItemClass(itemClass);
        item.setLength(length);
        item.setWidth(width);
        item.setHeight(height);
        item.setWeight(weight);
        item.setCube(cube);
        item.setStorageType(storageType);
        item.setStorageZone(storageZone);
        item.setAbcClass(abcClass);
        if (lotControlled != null) item.setLotControlled(lotControlled);
        if (expirationControlled != null) item.setExpirationControlled(expirationControlled);
        if (shelfLife != null) item.setShelfLife(shelfLife);
        if (hazmat != null) item.setHazmat(hazmat);
        item.setHazmatClass(hazmatClass);
        return item;
    }
}
