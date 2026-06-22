package com.maersk.wms.inventory.infrastructure;

import com.maersk.wms.inventory.domain.LotxLocxId;
import com.maersk.wms.inventory.domain.InventoryStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for LOTxLOCxID persistence.
 */
public interface LotxLocxIdRepository {

    Optional<LotxLocxId> findById(String lotxLocxIdKey);

    List<LotxLocxId> findBySku(String sku, String warehouse);

    List<LotxLocxId> findAvailableBySku(String sku, String warehouse);

    List<LotxLocxId> findByLocation(String location, String warehouse);

    List<LotxLocxId> findByLpn(String lpn, String warehouse);

    List<LotxLocxId> findByLot(String lot, String sku, String warehouse);

    LotxLocxId save(LotxLocxId inventory);

    void updateQty(String lotxLocxIdKey, BigDecimal newQty);

    void updateStatus(String lotxLocxIdKey, InventoryStatus status);

    void updateAllocatedQty(String lotxLocxIdKey, BigDecimal allocatedQty);

    void delete(String lotxLocxIdKey);

    BigDecimal getTotalAvailableQty(String sku, String warehouse);
}
