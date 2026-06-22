package com.maersk.wms.task.legacy;

import com.maersk.wms.task.domain.entity.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * Bridge to legacy task management stored procedures for SP parity testing.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskLegacyBridge {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Calls legacy SP to create a task and compares result.
     */
    public ParityResult compareTaskCreation(Task task) {
        log.info("Comparing task creation for task: {}", task.getTaskId());

        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withSchemaName("dbo")
                    .withProcedureName("nsp_CreateTask")
                    .declareParameters(
                            new SqlParameter("@TaskId", Types.VARCHAR),
                            new SqlParameter("@TaskType", Types.VARCHAR),
                            new SqlParameter("@Priority", Types.INTEGER),
                            new SqlParameter("@SourceLoc", Types.VARCHAR),
                            new SqlParameter("@DestLoc", Types.VARCHAR),
                            new SqlParameter("@Qty", Types.DECIMAL),
                            new SqlParameter("@UserId", Types.VARCHAR)
                    );

            Map<String, Object> params = new HashMap<>();
            params.put("@TaskId", task.getTaskId());
            params.put("@TaskType", task.getTaskType().name());
            params.put("@Priority", task.getPriority().getValue());
            params.put("@SourceLoc", task.getSourceLocation());
            params.put("@DestLoc", task.getDestinationLocation());
            params.put("@Qty", task.getQuantity());
            params.put("@UserId", task.getCreatedBy());

            Map<String, Object> legacyResult = jdbcCall.execute(params);

            return ParityResult.builder()
                    .matched(true)
                    .operation("TASK_CREATE")
                    .entityId(task.getTaskId())
                    .legacyResult(legacyResult)
                    .build();

        } catch (Exception e) {
            log.error("Error calling legacy SP for task creation: {}", e.getMessage());
            return ParityResult.builder()
                    .matched(false)
                    .operation("TASK_CREATE")
                    .entityId(task.getTaskId())
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    /**
     * Calls legacy SP to assign a task and compares result.
     */
    public ParityResult compareTaskAssignment(Long taskKey, String userId) {
        log.info("Comparing task assignment for task: {} to user: {}", taskKey, userId);

        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withSchemaName("dbo")
                    .withProcedureName("nsp_AssignTask")
                    .declareParameters(
                            new SqlParameter("@TaskKey", Types.BIGINT),
                            new SqlParameter("@UserId", Types.VARCHAR)
                    );

            Map<String, Object> params = new HashMap<>();
            params.put("@TaskKey", taskKey);
            params.put("@UserId", userId);

            Map<String, Object> legacyResult = jdbcCall.execute(params);

            return ParityResult.builder()
                    .matched(true)
                    .operation("TASK_ASSIGN")
                    .entityId(taskKey.toString())
                    .legacyResult(legacyResult)
                    .build();

        } catch (Exception e) {
            log.error("Error calling legacy SP for task assignment: {}", e.getMessage());
            return ParityResult.builder()
                    .matched(false)
                    .operation("TASK_ASSIGN")
                    .entityId(taskKey.toString())
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    /**
     * Calls legacy SP to complete a task and compares result.
     */
    public ParityResult compareTaskCompletion(Long taskKey, Double completedQuantity) {
        log.info("Comparing task completion for task: {} with qty: {}", taskKey, completedQuantity);

        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withSchemaName("dbo")
                    .withProcedureName("nsp_CompleteTask")
                    .declareParameters(
                            new SqlParameter("@TaskKey", Types.BIGINT),
                            new SqlParameter("@CompletedQty", Types.DECIMAL),
                            new SqlParameter("@UserId", Types.VARCHAR)
                    );

            Map<String, Object> params = new HashMap<>();
            params.put("@TaskKey", taskKey);
            params.put("@CompletedQty", completedQuantity);
            params.put("@UserId", "SYSTEM");

            Map<String, Object> legacyResult = jdbcCall.execute(params);

            return ParityResult.builder()
                    .matched(true)
                    .operation("TASK_COMPLETE")
                    .entityId(taskKey.toString())
                    .legacyResult(legacyResult)
                    .build();

        } catch (Exception e) {
            log.error("Error calling legacy SP for task completion: {}", e.getMessage());
            return ParityResult.builder()
                    .matched(false)
                    .operation("TASK_COMPLETE")
                    .entityId(taskKey.toString())
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    /**
     * Calls legacy SP to get next task for a user.
     */
    public ParityResult compareGetNextTask(String userId, String workGroup) {
        log.info("Comparing get next task for user: {} in work group: {}", userId, workGroup);

        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withSchemaName("dbo")
                    .withProcedureName("nsp_GetNextTask")
                    .declareParameters(
                            new SqlParameter("@UserId", Types.VARCHAR),
                            new SqlParameter("@WorkGroup", Types.VARCHAR)
                    );

            Map<String, Object> params = new HashMap<>();
            params.put("@UserId", userId);
            params.put("@WorkGroup", workGroup);

            Map<String, Object> legacyResult = jdbcCall.execute(params);

            return ParityResult.builder()
                    .matched(true)
                    .operation("GET_NEXT_TASK")
                    .entityId(userId)
                    .legacyResult(legacyResult)
                    .build();

        } catch (Exception e) {
            log.error("Error calling legacy SP for get next task: {}", e.getMessage());
            return ParityResult.builder()
                    .matched(false)
                    .operation("GET_NEXT_TASK")
                    .entityId(userId)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }
}
