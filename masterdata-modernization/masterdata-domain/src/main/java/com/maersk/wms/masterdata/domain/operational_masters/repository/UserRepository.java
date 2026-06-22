package com.maersk.wms.masterdata.domain.operational_masters.repository;

import com.maersk.wms.masterdata.domain.operational_masters.model.User;
import com.maersk.wms.masterdata.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity.
 */
public interface UserRepository {

    User save(User user);

    Optional<User> findByUserKey(UserKey userKey);

    Optional<User> findByUserId(String userId);

    Optional<User> findByBadgeNumber(String badgeNumber);

    List<User> findByWarehouse(WarehouseKey warehouseKey);

    List<User> findByWarehouseAndStatus(WarehouseKey warehouseKey, User.UserStatus status);

    List<User> findActiveByWarehouse(WarehouseKey warehouseKey);

    List<User> findAvailableByWarehouse(WarehouseKey warehouseKey);

    List<User> findByZone(ZoneKey zoneKey);

    List<User> findAvailableByZone(ZoneKey zoneKey);

    List<User> findByShift(WarehouseKey warehouseKey, String shift);

    List<User> findByDepartment(WarehouseKey warehouseKey, String department);

    List<User> findBySupervisor(String supervisor);

    List<User> findByRole(String role);

    List<User> search(WarehouseKey warehouseKey, String searchTerm, int limit, int offset);

    boolean existsByUserId(String userId);

    void delete(User user);

    int countByWarehouseAndStatus(WarehouseKey warehouseKey, User.UserStatus status);
}
