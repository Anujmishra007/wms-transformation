package com.maersk.wms.masterdata.api;

import com.maersk.wms.masterdata.domain.Item;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response for item operations.
 */
@Data
@Builder
public class ItemResponse {

    private Long id;
    private String sku;
    private String description;
    private String descriptionLong;
    private String itemType;
    private String status;
    private String itemGroup;
    private String itemClass;
    private String itemFamily;

    // Physical
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
    private String abcClass;
    private String velocityCode;

    // Control
    private boolean lotControlled;
    private boolean serialControlled;
    private boolean expirationControlled;
    private int shelfLife;

    // Hazmat
    private boolean hazmat;
    private String hazmatClass;

    // Catch weight
    private boolean catchWeight;
    private BigDecimal minWeight;
    private BigDecimal maxWeight;

    // Audit
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    public static ItemResponse fromEntity(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .sku(item.getSku())
                .description(item.getDescription())
                .descriptionLong(item.getDescriptionLong())
                .itemType(item.getItemType() != null ? item.getItemType().name() : null)
                .status(item.getStatus() != null ? item.getStatus().name() : null)
                .itemGroup(item.getItemGroup())
                .itemClass(item.getItemClass())
                .itemFamily(item.getItemFamily())
                .length(item.getLength())
                .width(item.getWidth())
                .height(item.getHeight())
                .dimensionUom(item.getDimensionUom())
                .weight(item.getWeight())
                .weightUom(item.getWeightUom())
                .cube(item.getCube())
                .storageType(item.getStorageType())
                .storageZone(item.getStorageZone())
                .abcClass(item.getAbcClass())
                .velocityCode(item.getVelocityCode())
                .lotControlled(item.isLotControlled())
                .serialControlled(item.isSerialControlled())
                .expirationControlled(item.isExpirationControlled())
                .shelfLife(item.getShelfLife())
                .hazmat(item.isHazmat())
                .hazmatClass(item.getHazmatClass())
                .catchWeight(item.isCatchWeight())
                .minWeight(item.getMinWeight())
                .maxWeight(item.getMaxWeight())
                .createdAt(item.getCreatedAt())
                .createdBy(item.getCreatedBy())
                .updatedAt(item.getUpdatedAt())
                .updatedBy(item.getUpdatedBy())
                .build();
    }
}
