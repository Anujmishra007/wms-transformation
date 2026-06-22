package com.maersk.wms.picking.infrastructure;

import com.maersk.wms.picking.domain.PickTask;
import com.maersk.wms.picking.domain.TaskStatus;
import com.maersk.wms.picking.plugin.GetTaskCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of TaskRepository.
 * Uses JdbcTemplate following existing WMS patterns.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcTaskRepository implements TaskRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<PickTask> rowMapper = new TaskRowMapper();

    @Override
    public Optional<PickTask> findById(String taskId) {
        String sql = """
            SELECT t.*, pd.SKU, pd.LOT, pd.FROMLOC, pd.TOLOC, pd.FROMID, pd.TOID, pd.QTY
            FROM TASKDETAIL t
            JOIN PICKDETAIL pd ON t.TASKDETAILKEY = pd.TASKDETAILKEY
            WHERE t.TASKDETAILKEY = ?
            """;
        List<PickTask> tasks = jdbcTemplate.query(sql, rowMapper, taskId);
        return tasks.isEmpty() ? Optional.empty() : Optional.of(tasks.get(0));
    }

    @Override
    public List<PickTask> findAvailableTasks(GetTaskCriteria criteria) {
        StringBuilder sql = new StringBuilder("""
            SELECT TOP (?)
                t.*, pd.SKU, pd.LOT, pd.FROMLOC, pd.TOLOC, pd.FROMID, pd.TOID, pd.QTY
            FROM TASKDETAIL t
            JOIN PICKDETAIL pd ON t.TASKDETAILKEY = pd.TASKDETAILKEY
            WHERE t.STATUS = 1
            """);

        if (criteria.getZones() != null && !criteria.getZones().isEmpty()) {
            sql.append(" AND t.ZONE IN (?)");
        }

        sql.append(" ORDER BY t.PRIORITY DESC, t.ADDDATE ASC");

        return jdbcTemplate.query(sql.toString(), rowMapper, criteria.getMaxTasks());
    }

    @Override
    public List<PickTask> findByAssignedUser(String userId) {
        String sql = """
            SELECT t.*, pd.SKU, pd.LOT, pd.FROMLOC, pd.TOLOC, pd.FROMID, pd.TOID, pd.QTY
            FROM TASKDETAIL t
            JOIN PICKDETAIL pd ON t.TASKDETAILKEY = pd.TASKDETAILKEY
            WHERE t.ASSIGNEDUSER = ?
            AND t.STATUS IN (2, 3)
            ORDER BY t.PRIORITY DESC
            """;
        return jdbcTemplate.query(sql, rowMapper, userId);
    }

    @Override
    public PickTask save(PickTask task) {
        // Implement save logic
        log.info("Saving task: {}", task.getTaskId());
        return task;
    }

    @Override
    public void updateStatus(String taskId, TaskStatus status) {
        String sql = "UPDATE TASKDETAIL SET STATUS = ?, EDITDATE = GETDATE() WHERE TASKDETAILKEY = ?";
        jdbcTemplate.update(sql, status.getLegacyCode(), taskId);
    }

    @Override
    public void assignTask(String taskId, String userId) {
        String sql = "UPDATE TASKDETAIL SET ASSIGNEDUSER = ?, STATUS = 2, ASSIGNDATE = GETDATE() WHERE TASKDETAILKEY = ?";
        jdbcTemplate.update(sql, userId, taskId);
    }

    private static class TaskRowMapper implements RowMapper<PickTask> {
        @Override
        public PickTask mapRow(ResultSet rs, int rowNum) throws SQLException {
            return PickTask.builder()
                    .taskId(rs.getString("TASKDETAILKEY"))
                    .orderId(rs.getString("ORDERKEY"))
                    .sku(rs.getString("SKU"))
                    .lot(rs.getString("LOT"))
                    .fromLocation(rs.getString("FROMLOC"))
                    .toLocation(rs.getString("TOLOC"))
                    .lpn(rs.getString("FROMID"))
                    .requestedQty(rs.getBigDecimal("QTY"))
                    .status(TaskStatus.fromLegacyCode(rs.getInt("STATUS")))
                    .assignedUser(rs.getString("ASSIGNEDUSER"))
                    .priority(rs.getInt("PRIORITY"))
                    .zone(rs.getString("ZONE"))
                    .build();
        }
    }
}
