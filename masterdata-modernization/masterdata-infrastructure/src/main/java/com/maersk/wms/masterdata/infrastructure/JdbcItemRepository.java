package com.maersk.wms.masterdata.infrastructure;

import com.maersk.wms.masterdata.domain.Item;
import com.maersk.wms.masterdata.domain.ItemStatus;
import com.maersk.wms.masterdata.domain.ItemType;
import com.maersk.wms.masterdata.domain.repository.ItemRepository;
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
 * JDBC implementation of ItemRepository.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcItemRepository implements ItemRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            return insert(item);
        }
        return update(item);
    }

    @Override
    public Optional<Item> findById(Long id) {
        String sql = "SELECT * FROM SKU WHERE SKU_ID = ?";
        List<Item> results = jdbcTemplate.query(sql, new ItemRowMapper(), id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Optional<Item> findBySku(String sku) {
        String sql = "SELECT * FROM SKU WHERE SKU = ?";
        List<Item> results = jdbcTemplate.query(sql, new ItemRowMapper(), sku);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Item> findByStatus(ItemStatus status) {
        String sql = "SELECT * FROM SKU WHERE STATUS = ?";
        return jdbcTemplate.query(sql, new ItemRowMapper(), status.getCode());
    }

    @Override
    public List<Item> findByItemGroup(String itemGroup) {
        String sql = "SELECT * FROM SKU WHERE SKUGROUP = ?";
        return jdbcTemplate.query(sql, new ItemRowMapper(), itemGroup);
    }

    @Override
    public List<Item> findByItemClass(String itemClass) {
        String sql = "SELECT * FROM SKU WHERE CLASS = ?";
        return jdbcTemplate.query(sql, new ItemRowMapper(), itemClass);
    }

    @Override
    public List<Item> searchBySkuOrDescription(String searchTerm) {
        String sql = "SELECT * FROM SKU WHERE SKU LIKE ? OR DESCR LIKE ?";
        String pattern = "%" + searchTerm + "%";
        return jdbcTemplate.query(sql, new ItemRowMapper(), pattern, pattern);
    }

    @Override
    public List<Item> findAll() {
        String sql = "SELECT * FROM SKU";
        return jdbcTemplate.query(sql, new ItemRowMapper());
    }

    @Override
    public void delete(Item item) {
        String sql = "DELETE FROM SKU WHERE SKU_ID = ?";
        jdbcTemplate.update(sql, item.getId());
    }

    @Override
    public boolean existsBySku(String sku) {
        String sql = "SELECT COUNT(*) FROM SKU WHERE SKU = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, sku);
        return count != null && count > 0;
    }

    private Item insert(Item item) {
        String keyQuery = "EXEC nspg_GetKey 'SKU', 1";
        Long newId = jdbcTemplate.queryForObject(keyQuery, Long.class);
        item.setId(newId);

        String sql = """
            INSERT INTO SKU (SKU_ID, SKU, DESCR, SKUGROUP, CLASS, ITEMTYPE, STATUS,
                STDGROSSWGT, STDNETWGT, STDCUBE, LWHUOM, WGTUOM, CUBEUOM,
                LOTTABLEVALIDATION, CATCHWEIGHT, SHELFLIFE, HAZMAT,
                ADDWHO, ADDDATE)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())
            """;

        jdbcTemplate.update(sql,
                item.getId(),
                item.getSku(),
                item.getDescription(),
                item.getItemGroup(),
                item.getItemClass(),
                item.getItemType() != null ? item.getItemType().getCode() : null,
                item.getStatus() != null ? item.getStatus().getCode() : "A",
                item.getWeight(),
                item.getWeight(),
                item.getCube(),
                item.getDimensionUom(),
                item.getWeightUom(),
                item.getCubeUom(),
                item.isLotControlled() ? "Y" : "N",
                item.isCatchWeight() ? "Y" : "N",
                item.getShelfLife(),
                item.isHazmat() ? "Y" : "N",
                item.getCreatedBy()
        );

        return item;
    }

    private Item update(Item item) {
        String sql = """
            UPDATE SKU SET
                DESCR = ?,
                SKUGROUP = ?,
                CLASS = ?,
                STATUS = ?,
                STDGROSSWGT = ?,
                STDCUBE = ?,
                LOTTABLEVALIDATION = ?,
                CATCHWEIGHT = ?,
                SHELFLIFE = ?,
                HAZMAT = ?,
                EDITWHO = ?,
                EDITDATE = GETDATE()
            WHERE SKU_ID = ?
            """;

        jdbcTemplate.update(sql,
                item.getDescription(),
                item.getItemGroup(),
                item.getItemClass(),
                item.getStatus() != null ? item.getStatus().getCode() : "A",
                item.getWeight(),
                item.getCube(),
                item.isLotControlled() ? "Y" : "N",
                item.isCatchWeight() ? "Y" : "N",
                item.getShelfLife(),
                item.isHazmat() ? "Y" : "N",
                item.getUpdatedBy(),
                item.getId()
        );

        return item;
    }

    private static class ItemRowMapper implements RowMapper<Item> {
        @Override
        public Item mapRow(ResultSet rs, int rowNum) throws SQLException {
            Item item = new Item();
            item.setId(rs.getLong("SKU_ID"));
            item.setSku(rs.getString("SKU"));
            item.setDescription(rs.getString("DESCR"));
            item.setItemGroup(rs.getString("SKUGROUP"));
            item.setItemClass(rs.getString("CLASS"));
            item.setItemType(ItemType.fromCode(rs.getString("ITEMTYPE")));
            item.setStatus(ItemStatus.fromCode(rs.getString("STATUS")));
            item.setWeight(rs.getBigDecimal("STDGROSSWGT"));
            item.setCube(rs.getBigDecimal("STDCUBE"));
            item.setLotControlled("Y".equals(rs.getString("LOTTABLEVALIDATION")));
            item.setCatchWeight("Y".equals(rs.getString("CATCHWEIGHT")));
            item.setShelfLife(rs.getInt("SHELFLIFE"));
            item.setHazmat("Y".equals(rs.getString("HAZMAT")));
            item.setCreatedBy(rs.getString("ADDWHO"));
            if (rs.getTimestamp("ADDDATE") != null) {
                item.setCreatedAt(rs.getTimestamp("ADDDATE").toLocalDateTime());
            }
            return item;
        }
    }
}
