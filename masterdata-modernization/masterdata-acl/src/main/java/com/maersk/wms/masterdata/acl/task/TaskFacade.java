package com.maersk.wms.masterdata.acl.task;

import com.maersk.wms.masterdata.shared.kernel.identifiers.*;

import java.util.List;

/**
 * ACL facade for Task Service communication.
 * Provides master data queries needed by task operations.
 */
public interface TaskFacade {

    /**
     * Get active tasks for user.
     */
    int getActiveTaskCount(UserKey userKey);

    /**
     * Check if user has active tasks.
     */
    boolean userHasActiveTasks(UserKey userKey);

    /**
     * Get tasks pending at location.
     */
    int getTasksAtLocation(LocationKey locationKey);

    /**
     * Check if equipment is assigned to tasks.
     */
    boolean equipmentHasActiveTasks(EquipmentKey equipmentKey);

    /**
     * Get zones with pending tasks.
     */
    List<ZoneKey> getZonesWithPendingTasks(WarehouseKey warehouseKey);
}
