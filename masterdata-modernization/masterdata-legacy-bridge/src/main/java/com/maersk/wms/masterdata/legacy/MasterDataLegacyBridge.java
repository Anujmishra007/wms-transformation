package com.maersk.wms.masterdata.legacy;

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
 * Bridge to legacy stored procedures for master data operations.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MasterDataLegacyBridge {

    private final JdbcTemplate jdbcTemplate;

    public ParityResult callLegacyCreateItem(String sku, String description, String userId) {
        log.debug("Calling legacy create item SP: {}", sku);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("nsp_CreateSKU")
                .declareParameters(
                        new SqlParameter("p_SKU", Types.VARCHAR),
                        new SqlParameter("p_Description", Types.VARCHAR),
                        new SqlParameter("p_UserID", Types.VARCHAR),
                        new SqlOutParameter("p_ErrorCode", Types.INTEGER),
                        new SqlOutParameter("p_ErrorMessage", Types.VARCHAR)
                );

        Map<String, Object> result = jdbcCall.execute(
                Map.of(
                        "p_SKU", sku,
                        "p_Description", description,
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

    public ParityResult callLegacyCreateLocation(String locationCode, String zone, String userId) {
        log.debug("Calling legacy create location SP: {}", locationCode);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("nsp_CreateLOC")
                .declareParameters(
                        new SqlParameter("p_LOC", Types.VARCHAR),
                        new SqlParameter("p_Zone", Types.VARCHAR),
                        new SqlParameter("p_UserID", Types.VARCHAR),
                        new SqlOutParameter("p_ErrorCode", Types.INTEGER),
                        new SqlOutParameter("p_ErrorMessage", Types.VARCHAR)
                );

        Map<String, Object> result = jdbcCall.execute(
                Map.of(
                        "p_LOC", locationCode,
                        "p_Zone", zone,
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
}
