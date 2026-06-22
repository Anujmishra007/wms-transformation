package com.maersk.wms.picking.legacy;

import com.maersk.wms.picking.domain.PickTask;
import com.maersk.wms.picking.domain.PickConfirmation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * Bridge to legacy stored procedures for parity testing.
 *
 * Enables dual-write mode where both new Java code and legacy SP
 * are executed, with results compared for validation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LegacyPickingBridge {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Call legacy GetTask SP.
     */
    public Map<String, Object> callGetTaskSP(String userId, String zone, String warehouse) {
        log.info("Calling legacy rdtfnc_GetTask for user={}, zone={}", userId, zone);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("rdtfnc_GetTask");

        return jdbcCall.execute(Map.of(
                "UserID", userId,
                "Zone", zone,
                "Warehouse", warehouse
        ));
    }

    /**
     * Call legacy Decode SP.
     */
    public Map<String, Object> callDecodeSP(String barcode, String expectedType, String taskId) {
        log.info("Calling legacy rdtfnc_Decode for barcode={}, type={}", barcode, expectedType);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("rdtfnc_Decode");

        return jdbcCall.execute(Map.of(
                "Barcode", barcode,
                "ExpectedType", expectedType,
                "TaskID", taskId
        ));
    }

    /**
     * Call legacy Confirm SP.
     */
    public Map<String, Object> callConfirmSP(PickTask task, PickConfirmation confirmation) {
        log.info("Calling legacy rdtfnc_Confirm for task={}", task.getTaskId());

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("rdtfnc_Confirm");

        return jdbcCall.execute(Map.of(
                "TaskID", task.getTaskId(),
                "PickedQty", confirmation.getPickedQty(),
                "FromLPN", confirmation.getFromLpn() != null ? confirmation.getFromLpn() : "",
                "ToLPN", confirmation.getToLpn() != null ? confirmation.getToLpn() : "",
                "UserID", confirmation.getUserId()
        ));
    }

    /**
     * Call any legacy SP by name (for extension SP parity testing).
     */
    public Map<String, Object> callExtensionSP(String spName, Map<String, Object> params) {
        log.info("Calling legacy extension SP: {}", spName);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName(spName);

        return jdbcCall.execute(params);
    }
}
