package com.maersk.wms.outbound.legacy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.util.Map;

/**
 * Bridge to legacy stored procedures for outbound operations.
 * Used for SP parity testing and gradual migration.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OutboundLegacyBridge {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Call legacy allocation stored procedure.
     */
    public ParityResult callLegacyAllocateOrder(String orderNumber, String userId) {
        log.debug("Calling legacy allocation SP for order: {}", orderNumber);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("nsp_AllocateOrder")
                .declareParameters(
                        new SqlParameter("p_OrdersKey", Types.VARCHAR),
                        new SqlParameter("p_UserID", Types.VARCHAR),
                        new SqlOutParameter("p_ErrorCode", Types.INTEGER),
                        new SqlOutParameter("p_ErrorMessage", Types.VARCHAR)
                );

        Map<String, Object> result = jdbcCall.execute(
                Map.of(
                        "p_OrdersKey", orderNumber,
                        "p_UserID", userId
                )
        );

        return ParityResult.builder()
                .success((Integer) result.get("p_ErrorCode") == 0)
                .errorCode((Integer) result.get("p_ErrorCode"))
                .errorMessage((String) result.get("p_ErrorMessage"))
                .legacyResult(result)
                .build();
    }

    /**
     * Call legacy wave creation stored procedure.
     */
    public ParityResult callLegacyCreateWave(String waveType, String userId) {
        log.debug("Calling legacy create wave SP");

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("nsp_CreateWave")
                .declareParameters(
                        new SqlParameter("p_WaveType", Types.VARCHAR),
                        new SqlParameter("p_UserID", Types.VARCHAR),
                        new SqlOutParameter("p_WaveKey", Types.VARCHAR),
                        new SqlOutParameter("p_ErrorCode", Types.INTEGER),
                        new SqlOutParameter("p_ErrorMessage", Types.VARCHAR)
                );

        Map<String, Object> result = jdbcCall.execute(
                Map.of(
                        "p_WaveType", waveType,
                        "p_UserID", userId
                )
        );

        return ParityResult.builder()
                .success((Integer) result.get("p_ErrorCode") == 0)
                .errorCode((Integer) result.get("p_ErrorCode"))
                .errorMessage((String) result.get("p_ErrorMessage"))
                .resultKey((String) result.get("p_WaveKey"))
                .legacyResult(result)
                .build();
    }

    /**
     * Call legacy wave release stored procedure.
     */
    public ParityResult callLegacyReleaseWave(String waveNumber, String userId) {
        log.debug("Calling legacy release wave SP: {}", waveNumber);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("nsp_ReleaseWave")
                .declareParameters(
                        new SqlParameter("p_WaveKey", Types.VARCHAR),
                        new SqlParameter("p_UserID", Types.VARCHAR),
                        new SqlOutParameter("p_ErrorCode", Types.INTEGER),
                        new SqlOutParameter("p_ErrorMessage", Types.VARCHAR)
                );

        Map<String, Object> result = jdbcCall.execute(
                Map.of(
                        "p_WaveKey", waveNumber,
                        "p_UserID", userId
                )
        );

        return ParityResult.builder()
                .success((Integer) result.get("p_ErrorCode") == 0)
                .errorCode((Integer) result.get("p_ErrorCode"))
                .errorMessage((String) result.get("p_ErrorMessage"))
                .legacyResult(result)
                .build();
    }

    /**
     * Call legacy shipment creation stored procedure.
     */
    public ParityResult callLegacyCreateShipment(String orderNumber, String userId) {
        log.debug("Calling legacy create shipment SP for order: {}", orderNumber);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("nsp_CreateShipment")
                .declareParameters(
                        new SqlParameter("p_OrdersKey", Types.VARCHAR),
                        new SqlParameter("p_UserID", Types.VARCHAR),
                        new SqlOutParameter("p_ShipmentKey", Types.VARCHAR),
                        new SqlOutParameter("p_ErrorCode", Types.INTEGER),
                        new SqlOutParameter("p_ErrorMessage", Types.VARCHAR)
                );

        Map<String, Object> result = jdbcCall.execute(
                Map.of(
                        "p_OrdersKey", orderNumber,
                        "p_UserID", userId
                )
        );

        return ParityResult.builder()
                .success((Integer) result.get("p_ErrorCode") == 0)
                .errorCode((Integer) result.get("p_ErrorCode"))
                .errorMessage((String) result.get("p_ErrorMessage"))
                .resultKey((String) result.get("p_ShipmentKey"))
                .legacyResult(result)
                .build();
    }

    /**
     * Call legacy ship confirm stored procedure.
     */
    public ParityResult callLegacyShipConfirm(String shipmentId, String userId) {
        log.debug("Calling legacy ship confirm SP: {}", shipmentId);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("nsp_ShipConfirm")
                .declareParameters(
                        new SqlParameter("p_ShipmentKey", Types.VARCHAR),
                        new SqlParameter("p_UserID", Types.VARCHAR),
                        new SqlOutParameter("p_TrackingNumber", Types.VARCHAR),
                        new SqlOutParameter("p_ErrorCode", Types.INTEGER),
                        new SqlOutParameter("p_ErrorMessage", Types.VARCHAR)
                );

        Map<String, Object> result = jdbcCall.execute(
                Map.of(
                        "p_ShipmentKey", shipmentId,
                        "p_UserID", userId
                )
        );

        return ParityResult.builder()
                .success((Integer) result.get("p_ErrorCode") == 0)
                .errorCode((Integer) result.get("p_ErrorCode"))
                .errorMessage((String) result.get("p_ErrorMessage"))
                .resultKey((String) result.get("p_TrackingNumber"))
                .legacyResult(result)
                .build();
    }
}
