package com.maersk.wms.masterdata.domain.repository;

import com.maersk.wms.masterdata.domain.Item;
import com.maersk.wms.masterdata.domain.ItemStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Item entities.
 */
public interface ItemRepository {

    Item save(Item item);

    Optional<Item> findById(Long id);

    Optional<Item> findBySku(String sku);

    List<Item> findByStatus(ItemStatus status);

    List<Item> findByItemGroup(String itemGroup);

    List<Item> findByItemClass(String itemClass);

    List<Item> searchBySkuOrDescription(String searchTerm);

    List<Item> findAll();

    void delete(Item item);

    boolean existsBySku(String sku);
}
