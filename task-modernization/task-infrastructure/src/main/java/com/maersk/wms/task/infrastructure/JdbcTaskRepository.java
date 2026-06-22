package com.maersk.wms.task.infrastructure;

import com.maersk.wms.task.domain.entity.Task;
import com.maersk.wms.task.domain.enums.TaskStatus;
import com.maersk.wms.task.domain.enums.TaskType;
import com.maersk.wms.task.domain.enums.TaskPriority;
import com.maersk.wms.task.domain.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * JDBC implementation of TaskRepository.
 * Maps to TASKDETAIL table in WMS database.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcTaskRepository implements TaskRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Task> taskRowMapper = (rs, rowNum) -> mapTask(rs);

    @Override
    public Task save(Task task) {
        if (task.getTaskKey() == null) {
            return insert(task);
        } else {
            return update(task);
        }
    }

    private Task insert(Task task) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("TASKDETAIL")
                .usingGeneratedKeyColumns("TASKDETAILKEY");

        Map<String, Object> params = new HashMap<>();
        params.put("TASKID", task.getTaskId());
        params.put("TASKTYPE", task.getTaskType().name());
        params.put("STATUS", task.getStatus().getCode());
        params.put("PRIORITY", task.getPriority().getValue());
        params.put("SOURCELOC", task.getSourceLocation());
        params.put("SOURCEZONE", task.getSourceZone());
        params.put("SOURCELPN", task.getSourceLpn());
        params.put("DESTLOC", task.getDestinationLocation());
        params.put("DESTZONE", task.getDestinationZone());
        params.put("DESTLPN", task.getDestinationLpn());
        params.put("SKU", task.getSku());
        params.put("QTY", task.getQuantity());
        params.put("ASSIGNEDUSERID", task.getAssignedUserId());
        params.put("WORKGROUP", task.getWorkGroup());
        params.put("WORKZONE", task.getWorkZone());
        params.put("ORDERKEY", task.getOrderKey());
        params.put("WAVEID", task.getWaveId());
        params.put("ADDDATE", LocalDateTime.now());
        params.put("ADDWHO", task.getCreatedBy());

        Number key = insert.executeAndReturnKey(params);
        task.setTaskKey(key.longValue());
        return task;
    }

    private Task update(Task task) {
        String sql = """
            UPDATE TASKDETAIL SET
                STATUS = ?, PRIORITY = ?, ASSIGNEDUSERID = ?, ASSIGNEDUSERNAME = ?,
                QTY = ?, PICKEDQTY = ?, SHORTQTY = ?,
                STARTTIME = ?, ENDTIME = ?,
                EDITDATE = ?, EDITWHO = ?
            WHERE TASKDETAILKEY = ?
            """;

        jdbcTemplate.update(sql,
                task.getStatus().getCode(),
                task.getPriority().getValue(),
                task.getAssignedUserId(),
                task.getAssignedUserName(),
                task.getQuantity(),
                task.getPickedQuantity(),
                task.getShortQuantity(),
                task.getStartedAt(),
                task.getCompletedAt(),
                LocalDateTime.now(),
                task.getModifiedBy(),
                task.getTaskKey());

        return task;
    }

    @Override
    public Optional<Task> findByTaskKey(Long taskKey) {
        String sql = "SELECT * FROM TASKDETAIL WHERE TASKDETAILKEY = ?";
        List<Task> tasks = jdbcTemplate.query(sql, taskRowMapper, taskKey);
        return tasks.isEmpty() ? Optional.empty() : Optional.of(tasks.get(0));
    }

    @Override
    public Optional<Task> findByTaskId(String taskId) {
        String sql = "SELECT * FROM TASKDETAIL WHERE TASKID = ?";
        List<Task> tasks = jdbcTemplate.query(sql, taskRowMapper, taskId);
        return tasks.isEmpty() ? Optional.empty() : Optional.of(tasks.get(0));
    }

    @Override
    public List<Task> findByStatus(TaskStatus status) {
        String sql = "SELECT * FROM TASKDETAIL WHERE STATUS = ? ORDER BY PRIORITY, ADDDATE";
        return jdbcTemplate.query(sql, taskRowMapper, status.getCode());
    }

    @Override
    public List<Task> findByTaskType(TaskType taskType) {
        String sql = "SELECT * FROM TASKDETAIL WHERE TASKTYPE = ? ORDER BY PRIORITY, ADDDATE";
        return jdbcTemplate.query(sql, taskRowMapper, taskType.name());
    }

    @Override
    public List<Task> findByAssignedUserId(String userId) {
        String sql = "SELECT * FROM TASKDETAIL WHERE ASSIGNEDUSERID = ? AND STATUS NOT IN ('6', '8', 'F') ORDER BY PRIORITY, ADDDATE";
        return jdbcTemplate.query(sql, taskRowMapper, userId);
    }

    @Override
    public List<Task> findByStatusAndTaskType(TaskStatus status, TaskType taskType) {
        String sql = "SELECT * FROM TASKDETAIL WHERE STATUS = ? AND TASKTYPE = ? ORDER BY PRIORITY, ADDDATE";
        return jdbcTemplate.query(sql, taskRowMapper, status.getCode(), taskType.name());
    }

    @Override
    public List<Task> findByWorkGroup(String workGroup) {
        String sql = "SELECT * FROM TASKDETAIL WHERE WORKGROUP = ? ORDER BY PRIORITY, ADDDATE";
        return jdbcTemplate.query(sql, taskRowMapper, workGroup);
    }

    @Override
    public List<Task> findByWorkZone(String workZone) {
        String sql = "SELECT * FROM TASKDETAIL WHERE WORKZONE = ? ORDER BY PRIORITY, ADDDATE";
        return jdbcTemplate.query(sql, taskRowMapper, workZone);
    }

    @Override
    public List<Task> findBySourceLocation(String sourceLocation) {
        String sql = "SELECT * FROM TASKDETAIL WHERE SOURCELOC = ? ORDER BY PRIORITY, ADDDATE";
        return jdbcTemplate.query(sql, taskRowMapper, sourceLocation);
    }

    @Override
    public List<Task> findByDestinationLocation(String destinationLocation) {
        String sql = "SELECT * FROM TASKDETAIL WHERE DESTLOC = ? ORDER BY PRIORITY, ADDDATE";
        return jdbcTemplate.query(sql, taskRowMapper, destinationLocation);
    }

    @Override
    public List<Task> findByOrderKey(Long orderKey) {
        String sql = "SELECT * FROM TASKDETAIL WHERE ORDERKEY = ? ORDER BY SEQNO";
        return jdbcTemplate.query(sql, taskRowMapper, orderKey);
    }

    @Override
    public List<Task> findByWaveId(String waveId) {
        String sql = "SELECT * FROM TASKDETAIL WHERE WAVEID = ? ORDER BY PRIORITY, SEQNO";
        return jdbcTemplate.query(sql, taskRowMapper, waveId);
    }

    @Override
    public List<Task> findByRouteId(String routeId) {
        String sql = "SELECT * FROM TASKDETAIL WHERE ROUTEID = ? ORDER BY SEQNO";
        return jdbcTemplate.query(sql, taskRowMapper, routeId);
    }

    @Override
    public List<Task> findPendingTasksByPriority(TaskPriority minPriority) {
        String sql = "SELECT * FROM TASKDETAIL WHERE STATUS IN ('0', '1') AND PRIORITY <= ? ORDER BY PRIORITY, ADDDATE";
        return jdbcTemplate.query(sql, taskRowMapper, minPriority.getValue());
    }

    @Override
    public List<Task> findUnassignedTasks() {
        String sql = "SELECT * FROM TASKDETAIL WHERE STATUS IN ('0', '1') AND ASSIGNEDUSERID IS NULL ORDER BY PRIORITY, ADDDATE";
        return jdbcTemplate.query(sql, taskRowMapper);
    }

    @Override
    public List<Task> findUnassignedTasksByWorkZone(String workZone) {
        String sql = "SELECT * FROM TASKDETAIL WHERE STATUS IN ('0', '1') AND ASSIGNEDUSERID IS NULL AND WORKZONE = ? ORDER BY PRIORITY, ADDDATE";
        return jdbcTemplate.query(sql, taskRowMapper, workZone);
    }

    @Override
    public List<Task> findOverdueTasks(LocalDateTime dueDate) {
        String sql = "SELECT * FROM TASKDETAIL WHERE STATUS NOT IN ('6', '8', 'F') AND DUEDATE < ? ORDER BY DUEDATE";
        return jdbcTemplate.query(sql, taskRowMapper, dueDate);
    }

    @Override
    public List<Task> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT * FROM TASKDETAIL WHERE ADDDATE BETWEEN ? AND ? ORDER BY ADDDATE";
        return jdbcTemplate.query(sql, taskRowMapper, start, end);
    }

    @Override
    public int countByStatus(TaskStatus status) {
        String sql = "SELECT COUNT(*) FROM TASKDETAIL WHERE STATUS = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, status.getCode());
        return count != null ? count : 0;
    }

    @Override
    public int countByStatusAndWorkGroup(TaskStatus status, String workGroup) {
        String sql = "SELECT COUNT(*) FROM TASKDETAIL WHERE STATUS = ? AND WORKGROUP = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, status.getCode(), workGroup);
        return count != null ? count : 0;
    }

    @Override
    public int countByAssignedUserId(String userId) {
        String sql = "SELECT COUNT(*) FROM TASKDETAIL WHERE ASSIGNEDUSERID = ? AND STATUS NOT IN ('6', '8', 'F')";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null ? count : 0;
    }

    @Override
    public void updateStatus(Long taskKey, TaskStatus status, String modifiedBy) {
        String sql = "UPDATE TASKDETAIL SET STATUS = ?, EDITDATE = ?, EDITWHO = ? WHERE TASKDETAILKEY = ?";
        jdbcTemplate.update(sql, status.getCode(), LocalDateTime.now(), modifiedBy, taskKey);
    }

    @Override
    public void assignTask(Long taskKey, String userId, String userName) {
        String sql = "UPDATE TASKDETAIL SET ASSIGNEDUSERID = ?, ASSIGNEDUSERNAME = ?, ASSIGNEDDATE = ?, STATUS = '2' WHERE TASKDETAILKEY = ?";
        jdbcTemplate.update(sql, userId, userName, LocalDateTime.now(), taskKey);
    }

    @Override
    public void unassignTask(Long taskKey, String reason) {
        String sql = "UPDATE TASKDETAIL SET ASSIGNEDUSERID = NULL, ASSIGNEDUSERNAME = NULL, STATUS = '1' WHERE TASKDETAILKEY = ?";
        jdbcTemplate.update(sql, taskKey);
    }

    @Override
    public void completeTask(Long taskKey, Double completedQuantity, String completedBy) {
        String sql = "UPDATE TASKDETAIL SET STATUS = '6', PICKEDQTY = ?, ENDTIME = ?, EDITWHO = ?, EDITDATE = ? WHERE TASKDETAILKEY = ?";
        jdbcTemplate.update(sql, completedQuantity, LocalDateTime.now(), completedBy, LocalDateTime.now(), taskKey);
    }

    @Override
    public void deleteByTaskKey(Long taskKey) {
        String sql = "DELETE FROM TASKDETAIL WHERE TASKDETAILKEY = ?";
        jdbcTemplate.update(sql, taskKey);
    }

    @Override
    public boolean existsByTaskId(String taskId) {
        String sql = "SELECT COUNT(*) FROM TASKDETAIL WHERE TASKID = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, taskId);
        return count != null && count > 0;
    }

    private Task mapTask(ResultSet rs) throws SQLException {
        return Task.builder()
                .taskKey(rs.getLong("TASKDETAILKEY"))
                .taskId(rs.getString("TASKID"))
                .taskType(TaskType.valueOf(rs.getString("TASKTYPE")))
                .status(TaskStatus.fromCode(rs.getString("STATUS")))
                .priority(TaskPriority.fromValue(rs.getInt("PRIORITY")))
                .sourceLocation(rs.getString("SOURCELOC"))
                .sourceZone(rs.getString("SOURCEZONE"))
                .sourceLpn(rs.getString("SOURCELPN"))
                .destinationLocation(rs.getString("DESTLOC"))
                .destinationZone(rs.getString("DESTZONE"))
                .destinationLpn(rs.getString("DESTLPN"))
                .sku(rs.getString("SKU"))
                .quantity(rs.getDouble("QTY"))
                .assignedUserId(rs.getString("ASSIGNEDUSERID"))
                .assignedUserName(rs.getString("ASSIGNEDUSERNAME"))
                .workGroup(rs.getString("WORKGROUP"))
                .workZone(rs.getString("WORKZONE"))
                .orderKey(rs.getObject("ORDERKEY") != null ? rs.getLong("ORDERKEY") : null)
                .waveId(rs.getString("WAVEID"))
                .createdAt(rs.getTimestamp("ADDDATE") != null ? rs.getTimestamp("ADDDATE").toLocalDateTime() : null)
                .startedAt(rs.getTimestamp("STARTTIME") != null ? rs.getTimestamp("STARTTIME").toLocalDateTime() : null)
                .completedAt(rs.getTimestamp("ENDTIME") != null ? rs.getTimestamp("ENDTIME").toLocalDateTime() : null)
                .createdBy(rs.getString("ADDWHO"))
                .modifiedBy(rs.getString("EDITWHO"))
                .build();
    }
}
