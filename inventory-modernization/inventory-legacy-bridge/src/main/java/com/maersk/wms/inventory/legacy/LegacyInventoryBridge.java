package com.maersk.wms.inventory.legacy;

import com.maersk.wms.inventory.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * Bridge to legacy inventory stored procedures for parity testing.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LegacyInventoryBridge {

    private final JdbcTemplate jdbcTemplate;

    public Map<String, Object> callAdjustInventorySP(InventoryAdjustment adjustment) {
        log.info("Calling legacy nsp_AdjustInventory");

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("nsp_AdjustInventory");

        return jdbcCall.execute(Map.of(
                "SKU", adjustment.getSku(),
                "LOC", adjustment.getLocation(),
                "AdjQty", adjustment.getAdjustedQty(),
                "ReasonCode", adjustment.getReasonCode()
        ));
    }

    public Map<String, Object> callTransferInventorySP(InventoryTransfer transfer) {
        log.info("Calling legacy nsp_TransferInventory");

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("nsp_TransferInventory");

        return jdbcCall.execute(Map.of(
                "SKU", transfer.getSku(),
                "FromLoc", transfer.getFromLocation(),
                "ToLoc", transfer.getToLocation(),
                "Qty", transfer.getTransferQty()
        ));
    }

    public Map<String, Object> callAllocateInventorySP(String sku, java.math.BigDecimal qty, String warehouse) {
        log.info("Calling legacy nsp_AllocateInventory");

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("nsp_AllocateInventory");

        return jdbcCall.execute(Map.of(
                "SKU", sku,
                "Qty", qty,
                "Warehouse", warehouse
        ));
    }
}
