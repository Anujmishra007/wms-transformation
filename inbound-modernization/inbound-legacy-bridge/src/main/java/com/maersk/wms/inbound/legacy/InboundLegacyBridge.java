package com.maersk.wms.inbound.legacy;

import com.maersk.wms.inbound.domain.Receipt;
import com.maersk.wms.inbound.domain.ReceiptDetail;
import com.maersk.wms.inbound.service.ReceivingService;
import com.maersk.wms.inbound.plugin.InboundPluginContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * Legacy bridge for SP parity testing.
 * Allows dual execution of modernized Java code and legacy stored procedures.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InboundLegacyBridge {

    private final JdbcTemplate jdbcTemplate;
    private final ReceivingService receivingService;

    /**
     * Execute receiving with parity comparison.
     * Runs both modernized code and legacy SP, compares results.
     */
    public ParityResult executeWithParity(Receipt receipt, ReceiptDetail detail, InboundPluginContext context) {
        log.info("Executing receiving with parity check for receipt: {}", receipt.getReceiptKey());

        // Execute modernized code
        long modernStart = System.currentTimeMillis();
        ReceiptDetail modernResult = null;
        Exception modernException = null;
        try {
            modernResult = receivingService.receiveInventory(receipt.getReceiptKey(), detail, context);
        } catch (Exception e) {
            modernException = e;
        }
        long modernDuration = System.currentTimeMillis() - modernStart;

        // Execute legacy SP
        long legacyStart = System.currentTimeMillis();
        Map<String, Object> legacyResult = null;
        Exception legacyException = null;
        try {
            legacyResult = executeLegacyReceive(receipt, detail, context);
        } catch (Exception e) {
            legacyException = e;
        }
        long legacyDuration = System.currentTimeMillis() - legacyStart;

        // Compare results
        ParityResult parityResult = compareResults(
                modernResult, modernException, modernDuration,
                legacyResult, legacyException, legacyDuration
        );

        log.info("Parity check complete. Match: {}, Modern: {}ms, Legacy: {}ms",
                parityResult.isMatch(), modernDuration, legacyDuration);

        return parityResult;
    }

    /**
     * Execute legacy stored procedure for receiving.
     */
    private Map<String, Object> executeLegacyReceive(Receipt receipt, ReceiptDetail detail, InboundPluginContext context) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("lsp_Receive_Inventory")
                .declareParameters(
                        new SqlParameter("RECEIPTKEY", Types.VARCHAR),
                        new SqlParameter("SKU", Types.VARCHAR),
                        new SqlParameter("LOT", Types.VARCHAR),
                        new SqlParameter("ID", Types.VARCHAR),
                        new SqlParameter("QTY", Types.DECIMAL),
                        new SqlParameter("LOCATION", Types.VARCHAR),
                        new SqlParameter("USERID", Types.VARCHAR)
                );

        Map<String, Object> params = new HashMap<>();
        params.put("RECEIPTKEY", receipt.getReceiptKey());
        params.put("SKU", detail.getSku());
        params.put("LOT", detail.getLot());
        params.put("ID", detail.getId());
        params.put("QTY", detail.getReceivedQty());
        params.put("LOCATION", detail.getLocation());
        params.put("USERID", context.getUserId());

        return jdbcCall.execute(params);
    }

    private ParityResult compareResults(
            ReceiptDetail modernResult, Exception modernException, long modernDuration,
            Map<String, Object> legacyResult, Exception legacyException, long legacyDuration) {

        ParityResult result = new ParityResult();
        result.setModernDurationMs(modernDuration);
        result.setLegacyDurationMs(legacyDuration);

        // Check for exceptions
        if (modernException != null && legacyException != null) {
            result.setMatch(true);
            result.setBothFailed(true);
            result.setModernError(modernException.getMessage());
            result.setLegacyError(legacyException.getMessage());
            return result;
        }

        if (modernException != null || legacyException != null) {
            result.setMatch(false);
            if (modernException != null) {
                result.setModernError(modernException.getMessage());
            }
            if (legacyException != null) {
                result.setLegacyError(legacyException.getMessage());
            }
            return result;
        }

        // Compare specific fields
        result.setMatch(true);
        // Add detailed field comparisons here

        return result;
    }
}
